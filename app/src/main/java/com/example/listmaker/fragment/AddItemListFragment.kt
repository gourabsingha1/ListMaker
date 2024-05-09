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
import com.example.listmaker.model.Item
import com.example.listmaker.model.ItemList
import jp.wasabeef.blurry.Blurry

class AddItemListFragment(private val itemList: ItemList, private val onUpdate: (Item) -> Unit) : DialogFragment() {

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
            val view = inflater.inflate(R.layout.fragment_add_item_list, null)

            // Set up the EditText and Button
            val etAddItemList = view.findViewById<EditText>(R.id.etAddItemList)
            val btnAddItemListOk = view.findViewById<Button>(R.id.btnAddItemListOk)
            btnAddItemListOk.setOnClickListener {
                val name = etAddItemList.text.toString()
                onUpdate(Item(name = name, itemListId = itemList.id))
                dismiss()
            }
            val btnAddItemListCancel = view.findViewById<Button>(R.id.btnAddItemListCancel)
            btnAddItemListCancel.setOnClickListener {
                dismiss()
            }

            // Keyboard should open as soon as fragment starts
            etAddItemList.requestFocus()
            etAddItemList.postDelayed({
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etAddItemList, InputMethodManager.SHOW_IMPLICIT)
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