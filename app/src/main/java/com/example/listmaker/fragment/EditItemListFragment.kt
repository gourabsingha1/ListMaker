package com.example.listmaker.fragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.listmaker.R
import com.example.listmaker.model.ItemList
import jp.wasabeef.blurry.Blurry

class EditItemListFragment(private val itemList: ItemList, private val onUpdate: (ItemList) -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            // Apply the blur effect
            val rootView = activity?.window?.decorView?.findViewById<View>(android.R.id.content)
            if (rootView != null) {
                Blurry.with(context).radius(10).sampling(4).onto(rootView as ViewGroup)
            }

            // Inflate the custom layout
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_edit_item_list, null)

            // Set up the EditText and Button
            val etEditItemList = view.findViewById<EditText>(R.id.etEditItemList)
            etEditItemList.setText(itemList.name)
            val btnEditItemListOk = view.findViewById<Button>(R.id.btnEditItemListOk)
            btnEditItemListOk.setOnClickListener {
                itemList.name = etEditItemList.text.toString()
                onUpdate(itemList)
                dismiss()
            }
            val btnEditItemListCancel = view.findViewById<Button>(R.id.btnEditItemListCancel)
            btnEditItemListCancel.setOnClickListener {
                dismiss()
            }

            // Keyboard should open as soon as fragment starts
            etEditItemList.requestFocus()
            etEditItemList.postDelayed({
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etEditItemList, InputMethodManager.SHOW_IMPLICIT)
            }, 100)

            // Set the custom layout
            builder.setView(view)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // Remove the blur effect
        val rootView = activity?.window?.decorView?.findViewById<View>(android.R.id.content)
        if (rootView != null) {
            Blurry.delete(rootView as ViewGroup)
        }
    }
}