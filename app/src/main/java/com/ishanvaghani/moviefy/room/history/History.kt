package com.ishanvaghani.moviefy.room.history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "searchHistory")
data class History(
    val value: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
