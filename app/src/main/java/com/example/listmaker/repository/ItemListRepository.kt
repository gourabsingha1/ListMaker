package com.example.listmaker.repository

import androidx.lifecycle.LiveData
import com.example.listmaker.data.ItemListDao
import com.example.listmaker.model.Item
import com.example.listmaker.model.ItemList
import com.example.listmaker.model.ItemListWithItems

class ItemListRepository (private val itemListDao: ItemListDao) {

    fun getItemListsWithItems(): LiveData<List<ItemListWithItems>> {
        return itemListDao.getItemListsWithItems()
    }

    suspend fun upsert(itemList: ItemList): Long {
        return itemListDao.upsertItemList(itemList)
    }

    suspend fun delete(itemList: ItemList) {
        itemListDao.deleteItemList(itemList)
    }

    suspend fun upsertItem(item: Item) {
        itemListDao.upsertItem(item)
    }

    suspend fun deleteItem(item: Item) {
        itemListDao.deleteItem(item)
    }
}