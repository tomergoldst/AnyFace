package com.tomergoldst.anyface.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.tomergoldst.anyface.R

const val ARG_TITLE = "title"

class AlertDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments!!.getString(ARG_TITLE)

        return AlertDialog.Builder(context!!)
            .setTitle(title)
            .setPositiveButton(R.string.alert_dialog_ok) { dialog, which ->
                dismiss()
            }
            .create()
    }

    companion object {

        fun newInstance(title: String): AlertDialogFragment {
            val frag = AlertDialogFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            frag.arguments = args
            return frag
        }
    }
}