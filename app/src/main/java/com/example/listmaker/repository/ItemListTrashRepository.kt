package com.example.listmaker.repository

import androidx.lifecycle.LiveData
import com.example.listmaker.data.ItemListTrashDao
import com.example.listmaker.model.Item
import com.example.listmaker.model.ItemList
import com.example.listmaker.model.ItemListWithItems

class ItemListTrashRepository (private val itemListTrashDao: ItemListTrashDao) {

    fun getItemListsWithItems(): LiveData<List<ItemListWithItems>> {
        return itemListTrashDao.getItemListsWithItems()
    }

    suspend fun upsert(itemList: ItemList): Long {
        return itemListTrashDao.upsertItemList(itemList)
    }

    suspend fun delete(itemList: ItemList) {
        itemListTrashDao.deleteItemList(itemList)
    }

    suspend fun upsertItem(item: Item): Long {
        return itemListTrashDao.upsertItem(item)
    }

    suspend fun deleteItem(item: Item) {
        itemListTrashDao.deleteItem(item)
    }
}