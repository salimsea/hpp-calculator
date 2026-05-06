package com.seal.hppcalculator.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ProductCostEntity::class, IngredientEntity::class], version = 2, exportSchema = false)
abstract class HppDatabase : RoomDatabase() {
    abstract fun hppDao(): HppDao

    companion object {
        @Volatile
        private var Instance: HppDatabase? = null

        fun getDatabase(context: Context): HppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, HppDatabase::class.java, "hpp_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
