package com.ishanvaghani.moviefy.model

import com.google.gson.annotations.SerializedName

data class Movie(
    val title: String?,
    val name: String?,
    val id: Int,
    @SerializedName("backdrop_path")
    val backdropPath: String,
    @SerializedName("poster_path")
    val posterPath: String
)