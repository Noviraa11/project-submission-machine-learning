package com.dicoding.asclepius.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.asclepius.data.room.AppDatabase
import com.dicoding.asclepius.data.room.PredictionHistoryDao
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.dicoding.asclepius.R


class ResultActivity : AppCompatActivity() {
    private lateinit var predictionHistoryDao: PredictionHistoryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Initialize database and DAO
        val db = AppDatabase.getInstance(applicationContext)
        predictionHistoryDao = db.predictionHistoryDao()

        // Use lifecycleScope to collect data from the Flow
        lifecycleScope.launch {
            predictionHistoryDao.getAllHistory().collect { _ ->
            }
        }
    }
}
