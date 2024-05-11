package com.example.listmaker.fragment

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.listmaker.R
import com.example.listmaker.model.ItemListWithItems
import com.example.listmaker.viewmodel.ItemListTrashViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DeleteForeverFragment(private val selectedLists: ArrayList<ItemListWithItems>) :
    DialogFragment() {

    private lateinit var itemListTrashViewModel: ItemListTrashViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            // Inflate the custom layout
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_delete_forever, null)

            // Set ViewModel
            itemListTrashViewModel = ViewModelProvider(this)[ItemListTrashViewModel::class.java]

            // Set up the EditText and Button
            val btnDeleteForeverOk = view.findViewById<Button>(R.id.btnDeleteForeverOk)
            btnDeleteForeverOk.setOnClickListener {
                selectedLists.forEach { currentItemListWithItems ->
                    itemListTrashViewModel.deleteItemList(currentItemListWithItems.itemList!!)
                    currentItemListWithItems.items?.forEach { item ->
                        itemListTrashViewModel.deleteItem(item)
                    }
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    dismiss()
                }, 1500)
            }
            val btnAddItemListCancel = view.findViewById<Button>(R.id.btnDeleteForeverCancel)
            btnAddItemListCancel.setOnClickListener {
                dismiss()
            }

            // Set the custom layout
            builder.setView(view)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}