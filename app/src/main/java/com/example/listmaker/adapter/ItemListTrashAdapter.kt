package com.example.listmaker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listmaker.R
import com.example.listmaker.databinding.LayoutItemListTrashBinding
import com.example.listmaker.model.ItemListWithItems

class ItemListTrashAdapter(
    private val onRecoverItemList: (ItemListWithItems) -> Unit,
    private val onDeleteItemList: (ItemListWithItems) -> Unit
) : RecyclerView.Adapter<ItemListTrashAdapter.ItemListsViewHolder>() {

    private var allItemListsWithItems = listOf<ItemListWithItems>()

    inner class ItemListsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutItemListTrashBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_list_trash, parent, false)
        return ItemListsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemListsViewHolder, position: Int) {
        val currentItemListWithItems = allItemListsWithItems[position]
        holder.binding.apply {
            // Set itemList name
            tfItemListTrashName.text = currentItemListWithItems.itemList?.name

            // Recover itemList
            ivItemListTrashRecover.setOnClickListener {
                onRecoverItemList(currentItemListWithItems)
                notifyDataSetChanged()
            }

            // Delete itemList
            ivItemListTrashDelete.setOnClickListener {
                onDeleteItemList(currentItemListWithItems)
                notifyDataSetChanged()
            }

            // Set Adapter
            val itemTrashAdapter = ItemTrashAdapter(currentItemListWithItems.items!!)

            // Set Recyclerview
            rvItemTrash.apply {
                adapter = itemTrashAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }

        // Set background color
        holder.itemView.apply {
            when(position % 7) {
                0 -> setBackgroundResource(R.drawable.item_list_background1)
                1 -> setBackgroundResource(R.drawable.item_list_background2)
                2 -> setBackgroundResource(R.drawable.item_list_background3)
                3 -> setBackgroundResource(R.drawable.item_list_background4)
                4 -> setBackgroundResource(R.drawable.item_list_background5)
                5 -> setBackgroundResource(R.drawable.item_list_background6)
                6 -> setBackgroundResource(R.drawable.item_list_background7)
            }
        }

        // Apply custom margin
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (position == itemCount - 1) {
            layoutParams.bottomMargin = 200 // Set your desired marginBottom here
        }
    }

    override fun getItemCount(): Int {
        return allItemListsWithItems.size
    }

    fun updateItemListsWithItems(itemLists: List<ItemListWithItems>) {
        this.allItemListsWithItems = itemLists
        notifyDataSetChanged()
    }
}


