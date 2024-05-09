package com.example.listmaker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listmaker.R
import com.example.listmaker.databinding.LayoutItemTrashBinding
import com.example.listmaker.model.Item

class ItemTrashAdapter(
    private var allItems: MutableList<Item>
) :
    RecyclerView.Adapter<ItemTrashAdapter.ListsViewHolder>() {

    inner class ListsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutItemTrashBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_trash, parent, false)
        return ListsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListsViewHolder, position: Int) {
        val currentItem = allItems[position]
        holder.binding.apply {
            // Set item name
            tfItemTrashName.text = currentItem.name
        }
    }

    override fun getItemCount(): Int {
        return allItems.size
    }
}


