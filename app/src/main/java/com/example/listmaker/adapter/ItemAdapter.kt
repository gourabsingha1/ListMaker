package com.example.listmaker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.listmaker.R
import com.example.listmaker.databinding.LayoutItemBinding
import com.example.listmaker.fragment.EditItemFragment
import com.example.listmaker.model.Item

class ItemAdapter(
    private var allItems: MutableList<Item>,
    private val onDelete: (Item) -> Unit,
    private val onUpdate: (Item) -> Unit
) :
    RecyclerView.Adapter<ItemAdapter.ListsViewHolder>() {

    inner class ListsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item, parent, false)
        return ListsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListsViewHolder, position: Int) {
        val currentItem = allItems[position]
        holder.binding.apply {
            // Set item name
            tfItemName.text = currentItem.name

            // Update item
            tfItemName.setOnClickListener {
                val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
                val dialog = EditItemFragment(currentItem, onUpdate)
                dialog.show(fragmentManager, "UpdateItemDialogFragment")
            }

            // Delete item
            ivItemDelete.setOnClickListener {
                onDelete(currentItem)
                allItems.removeAt(position)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return allItems.size
    }

    fun updateItems(items: MutableList<Item>) {
        this.allItems = items
        notifyDataSetChanged()
    }
}


