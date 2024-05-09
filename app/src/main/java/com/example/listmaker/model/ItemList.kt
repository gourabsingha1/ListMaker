package com.example.listmaker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_list_table")
data class ItemList(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "itemListName")
    var name: String? = null,
    @ColumnInfo(name = "selected")
    var selected: Boolean? = null,
)