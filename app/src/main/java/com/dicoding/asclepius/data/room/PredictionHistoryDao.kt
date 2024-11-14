package com.dicoding.asclepius.data.room

import androidx.room.Dao
import androidx.room.Insert
import kotlinx.coroutines.flow.Flow
import androidx.room.Query
import com.dicoding.asclepius.data.model.PredictionHistory

@Dao
interface PredictionHistoryDao {
    @Query("SELECT * FROM prediction_history")
    fun getAllHistory(): Flow<List<PredictionHistory>>
}
