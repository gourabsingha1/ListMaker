package com.example.listmaker.model

import androidx.room.Embedded
import androidx.room.Relation

data class ItemListWithItems(
    @Embedded val itemList: ItemList? = null,
    @Relation(
        parentColumn = "id",
        entityColumn = "itemListId"
    )
    val items: MutableList<Item>? = null
)
