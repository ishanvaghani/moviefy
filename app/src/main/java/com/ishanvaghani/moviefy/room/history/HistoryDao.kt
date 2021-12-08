package com.ishanvaghani.moviefy.room.history

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: History)

    @Query("SELECT * FROM searchHistory")
    fun getAll(): Flow<List<History>>

    @Delete
    suspend fun delete(history: History)

    @Query("SELECT EXISTS(SELECT * FROM searchHistory WHERE value = :value)")
    suspend fun isHistoryExists(value : String) : Boolean
}