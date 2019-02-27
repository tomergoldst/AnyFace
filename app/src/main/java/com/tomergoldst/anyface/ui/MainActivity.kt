package com.tomergoldst.anyface.ui

import InjectorUtils
import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.work.WorkManager
import com.google.android.material.tabs.TabLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.tomergoldst.anyface.R
import com.tomergoldst.anyface.config.Config
import com.tomergoldst.anyface.workers.KEY_RESULT_TOTAL_PHOTOS
import com.tomergoldst.anyface.workers.KEY_RESULT_TOTAL_PHOTOS_WITH_FACES
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        val TAG: String = MainActivity::class.java.simpleName

    }

    private lateinit var mModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mModel = ViewModelProviders.of(this, InjectorUtils.getMainViewModelProvider(application))
            .get(MainViewModel::class.java)

        mModel.dataLoading.observe(this, Observer {
            detectButton.isEnabled = !it
            loadingProgressBar.visibility = if (it) View.VISIBLE else View.GONE
        })

        mModel.detectFacesWorkerDispatched.observe(this, Observer {
            WorkManager.getInstance().getWorkInfoByIdLiveData(it)
                .observe(this@MainActivity, Observer { workInfo ->
                    // Do something with the status
                    if (workInfo != null && workInfo.state.isFinished) {
                        detectionProgressBar.visibility = View.GONE

                        // get output data of the work
                        val totalPhotos = workInfo.outputData.getInt(KEY_RESULT_TOTAL_PHOTOS, 0)
                        val totalPhotosWithFaces =
                            workInfo.outputData.getInt(KEY_RESULT_TOTAL_PHOTOS_WITH_FACES, 0)

                        // show message to user
                        val message = getString(com.tomergoldst.anyface.R.string.detection_completed_message,
                            totalPhotosWithFaces, totalPhotos)
                        showDialog(message)

                    }

                })
        })

        viewPager.apply {
            adapter = MainPagerAdapter(supportFragmentManager)
            addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
            viewPager.offscreenPageLimit = 2
        }

        tabLayout.apply {
            tabGravity = TabLayout.GRAVITY_FILL
            setupWithViewPager(viewPager)
        }

        detectButton.setOnClickListener {
            detectionProgressBar.visibility = View.VISIBLE
            mModel.start()
        }

        // Ask for read storage permission
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    mModel.init()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(this@MainActivity, "Permission is needed", Toast.LENGTH_LONG).show()
                }
            })
            .check()
    }

    fun showDialog(message: String) {
        val newFragment = AlertDialogFragment.newInstance(message)
        newFragment.show(supportFragmentManager, "dialog")
    }

    inner class MainPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

        // Returns total number of pages
        override fun getCount(): Int {
            return 3
        }

        // Returns the fragment to display for that page
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> PhotosFragment.newInstance(Config.TYPE_ALL)
                1 -> PhotosFragment.newInstance(Config.TYPE_WITH_FACES)
                2 -> PhotosFragment.newInstance(Config.TYPE_WITHOUT_FACES)
                else -> throw RuntimeException("MainPagerAdapter position out of bounds")
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(com.tomergoldst.anyface.R.string.all)
                1 -> getString(com.tomergoldst.anyface.R.string.with)
                2 -> getString(com.tomergoldst.anyface.R.string.without)
                else -> null
            }
        }
    }

}
