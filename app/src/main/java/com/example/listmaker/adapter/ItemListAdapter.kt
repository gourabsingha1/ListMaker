package com.example.listmaker.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listmaker.ItemListActivity
import com.example.listmaker.MakeListActivity
import com.example.listmaker.R
import com.example.listmaker.databinding.LayoutItemListBinding
import com.example.listmaker.fragment.AddItemListFragment
import com.example.listmaker.fragment.EditItemListFragment
import com.example.listmaker.fragment.ItemListFragment
import com.example.listmaker.model.Item
import com.example.listmaker.model.ItemList
import com.example.listmaker.model.ItemListWithItems

class ItemListAdapter(
    private val itemListClickUpdateInterface: ItemListClickUpdateInterface,
    private val onUpdateItemList: (ItemList) -> Unit,
    private val onUpdateItem: (Item) -> Unit,
    private val onDeleteItemList: (ItemListWithItems) -> Unit,
    private val onDeleteItem: (Item) -> Unit,
    private val showMenuDeleteSelected: (Boolean) -> Unit
) : RecyclerView.Adapter<ItemListAdapter.ItemListsViewHolder>() {

    private var allItemListsWithItems = listOf<ItemListWithItems>()
    private var isEnable = false
    private val selectedItemListsWithItems = mutableListOf<Int>()

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
        holder.binding.apply {
            // Set itemList name
            tfItemListName.text = currentItemListWithItems.itemList?.name

            // Update item list
//            tfItemListName.setOnClickListener {
//                val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
//                val dialog = EditItemListFragment(currentItemListWithItems.itemList!!, onUpdateItemList)
//                dialog.show(fragmentManager, "UpdateItemDialogFragment")
//            }

            // Delete itemList
//            ivItemListDelete.setOnClickListener {
//                onDeleteItemList(currentItemListWithItems)
//                notifyDataSetChanged()
//            }

            // Set Adapter and Delete / Update item
            val itemAdapter = ItemAdapter(
                currentItemListWithItems.items!!,
                onDelete = {item ->
                    onDeleteItem(item)
                },
                onUpdate = { item ->
                    onUpdateItem(item)
                })

            // Select item
//            ivItemListSelect.setOnClickListener {
//
//            }

            // Add item
//            ivItemListAdd.setOnClickListener {
//                val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
//                val dialog = AddItemListFragment(currentItemListWithItems.itemList!!, onUpdateItem)
//                dialog.show(fragmentManager, "UpdateItemDialogFragment")
//            }

            // Set Recyclerview
            rvHomeItem.apply {
                adapter = itemAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }

        // Select on long press
        holder.itemView.setOnLongClickListener {
            selectItem(holder, currentItemListWithItems, position)
            true
        }

        holder.itemView.setOnClickListener {
            itemListClickUpdateInterface.onItemListClick(currentItemListWithItems)
//            val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
//            val dialog = ItemListFragment(currentItemListWithItems, onUpdateItemList, onDeleteItemList, onUpdateItem, onDeleteItem)
//            dialog.show(fragmentManager, "UpdateItemDialogFragment")

            // If already selected
//            if(currentItemListWithItems.itemList?.selected == true) {
//                selectedItemListsWithItems.removeAt(position)
//                currentItemListWithItems.itemList?.selected = false
//                holder.binding.ivItemListSelect.visibility = View.GONE
//                // If it was last itemList, then menu delete selected should be invisible
//                if(selectedItemListsWithItems.isEmpty()) {
//                    showMenuDeleteSelected(false)
//                    isEnable = false
//                }
//            }
//            // If enabled
//            else if(isEnable) {
//                selectItem(holder, currentItemListWithItems, position)
//            }
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
//        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
//        if (position == itemCount - 1) {
//            layoutParams.bottomMargin = 200 // Set your desired marginBottom here
//        }
    }

    private fun selectItem(holder: ItemListAdapter.ItemListsViewHolder, currentItemListWithItems: ItemListWithItems, position: Int) {
        isEnable = true
        selectedItemListsWithItems.add(position)
        currentItemListWithItems.itemList?.selected = true
        holder.binding.ivItemListSelect.visibility = View.VISIBLE
        showMenuDeleteSelected(true)
    }

    override fun getItemCount(): Int {
        return allItemListsWithItems.size
    }

    fun updateItemListsWithItems(itemLists: List<ItemListWithItems>) {
        this.allItemListsWithItems = itemLists
        notifyDataSetChanged()
    }

    fun deleteSelected() {
        if(selectedItemListsWithItems.isNotEmpty()) {
            selectedItemListsWithItems.forEach { position ->
                allItemListsWithItems[position].itemList?.selected = false
                onDeleteItemList(allItemListsWithItems[position])
            }
            isEnable = false
            selectedItemListsWithItems.clear()
        }
        notifyDataSetChanged()
    }

    interface ItemListClickUpdateInterface {
        fun onItemListClick(currentItemListWithItems: ItemListWithItems)
    }

//    override fun getFilter(): Filter {
//        return object : Filter() {
//            override fun performFiltering(charSequence: CharSequence): FilterResults {
//                var filteredList: MutableList<ItemListWithItems> = ArrayList()
//                if (charSequence.isEmpty()) {
//                    allItemListsWithItems.forEach { itemListWithItems ->
//                        filteredList.add(itemListWithItems)
//                    }
//                } else {
//                    val filterPattern = charSequence.toString().lowercase(Locale.ROOT).trim { it <= ' ' }
//                    allItemListsWithItems.forEach { itemListWithItems ->
//                        if (itemListWithItems.itemList?.name?.lowercase(Locale.ROOT)?.contains(filterPattern)!!) {
//                            filteredList.add(itemListWithItems)
//                        }
//                    }
//                }
//                val results = FilterResults()
//                results.values = filteredList
//                return results
//            }
//
//            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
//                allItemListsWithItems = emptyList()
//                allItemListsWithItems = filterResults.values as MutableList<ItemListWithItems>
//                notifyDataSetChanged()
//            }
//        }
//    }
}


