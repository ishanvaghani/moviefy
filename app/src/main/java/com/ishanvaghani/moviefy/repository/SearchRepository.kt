package com.ishanvaghani.moviefy.repository

import com.ishanvaghani.moviefy.room.history.History
import com.ishanvaghani.moviefy.room.history.HistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchRepository @Inject constructor(private val dao: HistoryDao) {

    suspend fun insert(history: History) = withContext(Dispatchers.IO) {
        if (!isHistoryExists(history.value))
            dao.insert(history)
    }

    suspend fun delete(history: History) = withContext(Dispatchers.IO) {
        dao.delete(history)
    }

    private suspend fun isHistoryExists(value: String): Boolean {
        return dao.isHistoryExists(value)
    }

    fun getAll(): Flow<List<History>> = dao.getAll()
}