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
abstract class ItemListDatabase: RoomDatabase() {

    abstract fun getItemListDao(): ItemListDao

    // Singleton prevents multiple instances of database opening at the same time
    companion object {
        @Volatile
        private var INSTANCE: ItemListDatabase? = null

        fun getDatabase(context: Context): ItemListDatabase {
            return INSTANCE ?: synchronized(this) {
                // if the INSTANCE is not null, then return it,
                // else create the database
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemListDatabase::class.java,
                    "item_list_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}