package com.tomergoldst.anyface.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.tomergoldst.anyface.R
import com.tomergoldst.anyface.model.Photo
import com.tomergoldst.anyface.utils.DimensionUtils
import kotlinx.android.synthetic.main.fragment_photos.*


class PhotosFragment : Fragment(),
    PhotosRecyclerListAdapter.OnAdapterInteractionListener {

    private lateinit var mModel: MainViewModel
    private lateinit var mListAdapter: PhotosRecyclerListAdapter

    companion object {
        val TAG: String = PhotosFragment::class.java.simpleName

        const val ARG_ROLE = "ARG_ROLE"

        fun newInstance(role: Int): PhotosFragment {
            val f  = PhotosFragment()
            val args = Bundle()
            args.putInt(ARG_ROLE, role)
            f.arguments = args
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mModel = activity?.run {
            ViewModelProviders.of(this, InjectorUtils.getMainViewModelProvider(application))
                .get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val role = arguments!!.get(ARG_ROLE)

        if (role == 0) {
            mModel.allPhotos.observe(this, Observer {
                setPhotos(it)
            })
        }

        if (role == 1) {
            mModel.withFacesPhotos.observe(this, Observer {
                setPhotos(it)
            })
        }

        if (role == 2) {
            mModel.withoutFacesPhotos.observe(this, Observer {
                setPhotos(it)
            })
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mListAdapter = PhotosRecyclerListAdapter(this)
        recyclerView.apply {
            recyclerView.layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(
                SimpleItemDecoration(
                    DimensionUtils.dp2Px(context, 8).toInt(),
                    DimensionUtils.dp2Px(context, 8).toInt()
                )
            )
            recyclerView.adapter = mListAdapter
        }

    }

    private fun setPhotos(photos: List<Photo>?) {
        if (photos.isNullOrEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }

        mListAdapter.submitList(photos)

    }

    override fun onImageClicked(photo: Photo) {
        Toast.makeText(context, photo.name, Toast.LENGTH_SHORT).show()
    }
}
