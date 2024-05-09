package com.example.listmaker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.listmaker.data.ItemListDatabase
import com.example.listmaker.model.Item
import com.example.listmaker.model.ItemList
import com.example.listmaker.model.ItemListWithItems
import com.example.listmaker.repository.ItemListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemListViewModel (application: Application) : AndroidViewModel(application) {

    val itemListsWithItems: LiveData<List<ItemListWithItems>>
    private val repository: ItemListRepository

    init {
        val dao = ItemListDatabase.getDatabase(application).getItemListDao()
        repository = ItemListRepository(dao)
        itemListsWithItems = repository.getItemListsWithItems()
    }

    fun insertItemListWithItems(itemListName: String, itemsFromAI: List<Item>) = viewModelScope.launch(Dispatchers.IO) {
        // Create a new ItemList and insert it into the database
        val itemList = ItemList(name = itemListName)
        val itemListId = repository.upsert(itemList)

        // Create new Items for each item from the AI and insert them into the database
        for (item in itemsFromAI) {
            item.itemListId = itemListId
            repository.upsertItem(item)
        }
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

    val searchQuery = MutableLiveData<String>()

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }
}