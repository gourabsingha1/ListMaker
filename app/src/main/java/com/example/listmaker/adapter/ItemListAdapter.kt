package com.example.listmaker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listmaker.R
import com.example.listmaker.databinding.LayoutItemListBinding
import com.example.listmaker.model.ItemList
import com.example.listmaker.model.ItemListWithItems

class ItemListAdapter(
    private val itemListHomeInterface: ItemListHomeInterface
) : RecyclerView.Adapter<ItemListAdapter.ItemListsViewHolder>() {

    var allItemListsWithItems = listOf<ItemListWithItems>()

    inner class ItemListsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutItemListBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_list, parent, false)
        return ItemListsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemListsViewHolder, position: Int) {
        val currentItemListWithItems = allItemListsWithItems[position]

        // Set itemList name and items
        holder.binding.apply {
            tvItemListName.text = currentItemListWithItems.itemList?.name
            var items = ""
            for ((count, item) in currentItemListWithItems.items!!.withIndex()) {
                items += item.name
                if(count + 1 == 4) {
                    break
                }
                items += "\n"
            }
            tvItemListItems.text = items
        }

        // Select on long press
        holder.itemView.setOnLongClickListener {
            itemListHomeInterface.onItemListLongPress(currentItemListWithItems.itemList?.id!!)
            true
        }

        // If selected, toggle select on single press. Else expand itemList
        holder.itemView.setOnClickListener {
            itemListHomeInterface.onItemListClick(currentItemListWithItems.itemList?.id!!)
        }

        // Set background color
//        holder.itemView.apply {
////            when(position % 7) {
////                0 -> setBackgroundResource(R.drawable.item_list_background1)
////                1 -> setBackgroundResource(R.drawable.item_list_background2)
////                2 -> setBackgroundResource(R.drawable.item_list_background3)
////                3 -> setBackgroundResource(R.drawable.item_list_background4)
////                4 -> setBackgroundResource(R.drawable.item_list_background5)
////                5 -> setBackgroundResource(R.drawable.item_list_background6)
////                6 -> setBackgroundResource(R.drawable.item_list_background7)
////            }
//            setBackgroundResource(R.drawable.item_list_background8)
//        }

        // Change border color on selection
        if(currentItemListWithItems.itemList?.selected == true) {
            holder.itemView.setBackgroundResource(R.drawable.item_list_background_selected)
        }
        else {
            holder.itemView.setBackgroundResource(R.drawable.item_list_background8)
        }
    }

    override fun getItemCount(): Int {
        return allItemListsWithItems.size
    }

    fun updateItemListsWithItems(itemLists: List<ItemListWithItems>) {
        this.allItemListsWithItems = itemLists
        notifyDataSetChanged()
    }

    interface ItemListHomeInterface {
        fun onItemListClick(itemListId: Long)
        fun onItemListLongPress(itemListId: Long)
    }
}


