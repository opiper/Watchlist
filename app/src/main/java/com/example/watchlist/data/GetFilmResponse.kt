package com.example.watchlist.data

import com.google.gson.annotations.SerializedName

data class GetFilmResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val movies: List<Film>,
    @SerializedName("total_pages") val pages: Int
)
