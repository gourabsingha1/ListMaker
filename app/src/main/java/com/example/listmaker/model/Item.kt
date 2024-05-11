package com.example.listmaker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_table")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "itemListId")
    var itemListId: Long? = null,
    @ColumnInfo(name = "itemName")
    var name: String? = null,
)