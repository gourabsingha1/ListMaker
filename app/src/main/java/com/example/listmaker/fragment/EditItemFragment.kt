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
import jp.wasabeef.blurry.Blurry

class EditItemFragment(private val item: Item, private val onUpdate: (Item) -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            // Inflate the custom layout
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_edit_item, null)

            // Set up the EditText and Button
            val etEditItem = view.findViewById<EditText>(R.id.etEditItem)
            etEditItem.setText(item.name)
            val btnEditItemOk = view.findViewById<Button>(R.id.btnEditItemOk)
            btnEditItemOk.setOnClickListener {
                item.name = etEditItem.text.toString()
                onUpdate(item)
                dismiss()
            }
            val btnEditItemCancel = view.findViewById<Button>(R.id.btnEditItemCancel)
            btnEditItemCancel.setOnClickListener {
                dismiss()
            }

            // Keyboard should open as soon as fragment starts
            etEditItem.requestFocus()
            etEditItem.postDelayed({
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etEditItem, InputMethodManager.SHOW_IMPLICIT)
            }, 100)

            // Set the custom layout
            builder.setView(view)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}