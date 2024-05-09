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
interface ItemListTrashDao {

    @Upsert
    suspend fun upsertItemList(itemList: ItemList): Long

    @Upsert
    suspend fun upsertItem(item: Item): Long

    @Delete
    suspend fun deleteItemList(itemList: ItemList)

    @Delete
    suspend fun deleteItem(item: Item)

    // Joining ItemLists with Items to fetch a list of itemLists along with their items
    @Transaction
    @Query("SELECT * FROM item_list_table")
    fun getItemListsWithItems(): LiveData<List<ItemListWithItems>>
}