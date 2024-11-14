package com.dicoding.asclepius.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.asclepius.data.model.PredictionHistory

@Database(entities = [PredictionHistory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun predictionHistoryDao(): PredictionHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "prediction_history_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
