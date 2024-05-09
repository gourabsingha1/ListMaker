package com.example.listmaker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.listmaker.model.Item
import com.example.listmaker.model.ItemList

@Database(
    entities = [ItemList::class, Item::class],
    version = 1,
    exportSchema = false
)
abstract class ItemListTrashDatabase: RoomDatabase() {

    abstract fun getItemListDao(): ItemListTrashDao

    // Singleton prevents multiple instances of database opening at the same time
    companion object {
        @Volatile
        private var INSTANCE: ItemListTrashDatabase? = null

        fun getDatabase(context: Context): ItemListTrashDatabase {
            return INSTANCE ?: synchronized(this) {
                // if the INSTANCE is not null, then return it,
                // else create the database
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemListTrashDatabase::class.java,
                    "item_list_trash_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}