package com.ishanvaghani.moviefy.room.history

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [History::class], version = 1, exportSchema = false)
abstract class HistoryDatabase: RoomDatabase() {

    abstract fun getHistoryDao(): HistoryDao
}