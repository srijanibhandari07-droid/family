package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.MentalSpaceCheckInEntity
import com.example.data.model.MicroActionEntity
import com.example.data.model.ReflectionEntity
import com.example.data.model.ResponsibilityEntity

@Database(
    entities = [
        ReflectionEntity::class,
        ResponsibilityEntity::class,
        MicroActionEntity::class,
        MentalSpaceCheckInEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun betweenUsDao(): BetweenUsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "between_us_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
