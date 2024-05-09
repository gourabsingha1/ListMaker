package com.example.listmaker.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listmaker.R
import com.example.listmaker.adapter.ItemAdapter
import com.example.listmaker.adapter.ItemListAdapter
import com.example.listmaker.databinding.FragmentItemListBinding
import com.example.listmaker.model.Item
import com.example.listmaker.model.ItemList
import com.example.listmaker.model.ItemListWithItems
import com.example.listmaker.viewmodel.ItemListTrashViewModel
import com.example.listmaker.viewmodel.ItemListViewModel
import jp.wasabeef.blurry.Blurry

class ItemListFragment(
    private val currentItemListWithItems: ItemListWithItems,
    private val onUpdateItemList: (ItemList) -> Unit,
    private val onDeleteItemList: (ItemListWithItems) -> Unit,
    private val onUpdateItem: (Item) -> Unit,
    private val onDeleteItem: (Item) -> Unit
) : DialogFragment() {

    private lateinit var itemAdapter: ItemAdapter

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
            val view = inflater.inflate(R.layout.fragment_item_list, null)

            setAdapter()

            // Set RecyclerView
            view.findViewById<RecyclerView>(R.id.rvItem).apply {
                adapter = itemAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            // Set up the TextView, add ItemList and Delete ItemList
            val tvItemListName = view.findViewById<TextView>(R.id.tvItemListName)
            tvItemListName.text = currentItemListWithItems.itemList?.name
            tvItemListName.setOnClickListener {
                val fragmentManager = (requireActivity()).supportFragmentManager
                val dialog = EditItemListFragment(currentItemListWithItems.itemList!!, onUpdateItemList)
                dialog.show(fragmentManager, "UpdateItemDialogFragment")
                dismiss() // Update name in place
            }
            val ivItemListAddItem = view.findViewById<ImageView>(R.id.ivItemListAddItem)
            ivItemListAddItem.setOnClickListener {
                val fragmentManager = (requireActivity()).supportFragmentManager
                val dialog = AddItemListFragment(currentItemListWithItems.itemList!!, onUpdateItem)
                dialog.show(fragmentManager, "UpdateItemDialogFragment")
            }

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

    // Set Adapter
    private fun setAdapter() {
        itemAdapter = ItemAdapter(
            currentItemListWithItems.items!!,
            onDelete = { item ->
                onDeleteItem(item)
                dismiss()
            },
            onUpdate = { item ->
                onUpdateItem(item)
                dismiss()
            })
    }
}