package com.example.listmaker.model

import android.os.Parcel
import android.os.Parcelable
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeValue(selected)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemList> {
        override fun createFromParcel(parcel: Parcel): ItemList {
            return ItemList(parcel)
        }

        override fun newArray(size: Int): Array<ItemList?> {
            return arrayOfNulls(size)
        }
    }

}