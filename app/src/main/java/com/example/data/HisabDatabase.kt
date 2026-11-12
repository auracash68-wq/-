package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ExpenseEntity::class], version = 1, exportSchema = false)
abstract class HisabDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: HisabDatabase? = null

        fun getInstance(context: Context): HisabDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HisabDatabase::class.java,
                    "hisab_khata.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
