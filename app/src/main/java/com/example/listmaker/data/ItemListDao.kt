package com.example.listmaker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.listmaker.model.Item
import com.example.listmaker.model.ItemList
import com.example.listmaker.model.ItemListWithItems

@Dao
interface ItemListDao {

    // ItemList CRUD Operations
    @Upsert // Adds new item list and updates existing item list
    suspend fun upsertItemList(itemList: ItemList): Long

    @Delete
    suspend fun deleteItemList(itemList: ItemList)

    // Item CRUD Operations
    @Upsert
    suspend fun upsertItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

//    @Query("SELECT * FROM item WHERE itemListId = :itemListId ORDER BY itemName ASC")
//    fun getItemsOrderedByNameAsc(itemListId: Int): LiveData<List<Item>>
//
//    @Query("SELECT * FROM item WHERE itemListId = :itemListId ORDER BY itemName ASC")
//    fun getItemsOrderedByNameDesc(itemListId: Int): LiveData<List<Item>>

    // Joining ItemLists with Items to fetch a list of itemLists along with their items
    @Transaction
    @Query("SELECT * FROM item_list_table")
    fun getItemListsWithItems(): LiveData<List<ItemListWithItems>>
}