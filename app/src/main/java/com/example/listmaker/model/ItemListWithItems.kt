package com.example.listmaker.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation

data class ItemListWithItems(
    @Embedded val itemList: ItemList? = null,
    @Relation(
        parentColumn = "id",
        entityColumn = "itemListId"
    )
    val items: MutableList<Item>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemListWithItems> {
        override fun createFromParcel(parcel: Parcel): ItemListWithItems {
            return ItemListWithItems(parcel)
        }

        override fun newArray(size: Int): Array<ItemListWithItems?> {
            return arrayOfNulls(size)
        }
    }
}