package com.example.listmaker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.listmaker.data.ItemListTrashDatabase
import com.example.listmaker.model.Item
import com.example.listmaker.model.ItemList
import com.example.listmaker.model.ItemListWithItems
import com.example.listmaker.repository.ItemListTrashRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemListTrashViewModel (application: Application) : AndroidViewModel(application) {

    val itemListsWithItems: LiveData<List<ItemListWithItems>>
    private val repository: ItemListTrashRepository

    init {
        val dao = ItemListTrashDatabase.getDatabase(application).getItemListDao()
        repository = ItemListTrashRepository(dao)
        itemListsWithItems = repository.getItemListsWithItems()
    }

    fun upsertItemList(itemList: ItemList) = viewModelScope.launch(Dispatchers.IO) {
        repository.upsert(itemList)
    }

    fun deleteItemList(itemList: ItemList) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(itemList)
    }

    fun upsertItem(item: Item) = viewModelScope.launch(Dispatchers.IO) {
        repository.upsertItem(item)
    }

    fun deleteItem(item: Item) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteItem(item)
    }

}