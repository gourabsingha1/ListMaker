package com.example.listmaker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listmaker.R
import com.example.listmaker.databinding.LayoutItemListHomeBinding
import com.example.listmaker.model.ItemListWithItems

class ItemListAdapter(
    private val itemListClickUpdateInterface: ItemListClickUpdateInterface,
    private val onDeleteItemList: (ItemListWithItems) -> Unit,
    private val showMenuDeleteSelected: (Boolean) -> Unit
) : RecyclerView.Adapter<ItemListAdapter.ItemListsViewHolder>() {

    private var allItemListsWithItems = listOf<ItemListWithItems>()
    private var isEnable = false
    private val selectedItemListsWithItems = mutableListOf<Int>()

    inner class ItemListsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutItemListHomeBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_list_home, parent, false)
        return ItemListsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemListsViewHolder, position: Int) {
        val currentItemListWithItems = allItemListsWithItems[position]
        holder.binding.apply {
            // Set itemList name and items
            tvItemListHomeName.text = currentItemListWithItems.itemList?.name
            var items = ""
            var count = 0
            for (item in currentItemListWithItems.items!!) {
                items += item.name
                if(++count == 4) {
                    break
                }
                items += "\n"
            }
            tvItemListHomeItems.text = items
        }

        // Select on long press
        holder.itemView.setOnLongClickListener {
            selectItem(holder, currentItemListWithItems, position)
            true
        }

        holder.itemView.setOnClickListener {
            itemListClickUpdateInterface.onItemListClick(position)
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
    }

    private fun selectItem(holder: ItemListAdapter.ItemListsViewHolder, currentItemListWithItems: ItemListWithItems, position: Int) {
        isEnable = true
        selectedItemListsWithItems.add(position)
        currentItemListWithItems.itemList?.selected = true
        holder.binding.ivItemListHomeSelect.visibility = View.VISIBLE
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
        fun onItemListClick(position: Int)
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


