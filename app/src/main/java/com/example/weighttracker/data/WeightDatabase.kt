package com.example.weighttracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        WeightEntity::class,
        StepEntity::class,
        FoodEntity::class,
        SummaryEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class WeightDatabase : RoomDatabase() {

    abstract fun weightDao(): WeightDao
    abstract fun stepDao(): StepDao

    abstract fun foodDao(): FoodDao

    abstract fun summaryDao(): SummaryDao

    companion object {
        @Volatile
        private var INSTANCE: WeightDatabase? = null

        fun getDatabase(context: Context): WeightDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeightDatabase::class.java,
                    "weight_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}