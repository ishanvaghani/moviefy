package com.ishanvaghani.moviefy.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Genre(
    val name: String,
    val id: Int
) : Parcelable