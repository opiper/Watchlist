package com.example.watchlist.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("movie/popular")
    fun getPopularFilms(
        @Query("api_key") apiKey: String = "3baf383589d324b2f4fe8fbdbf10275f",
        @Query("page") page: Int
    ): Call<GetFilmResponse>

    @GET("movie/top_rated")
    fun getTopRatedFilms(
        @Query("api_key") apiKey: String = "3baf383589d324b2f4fe8fbdbf10275f",
        @Query("page") page: Int
    ): Call<GetFilmResponse>

    @GET("movie/{id}")
    fun getFilmDetails(
        @Path("id") filmId: Long,
        @Query("api_key") apiKey: String = "3baf383589d324b2f4fe8fbdbf10275f",
    ): Call<Film>

    @GET("search/movie")
    fun getFilmSearch(
        @Query("query") query: String,
        @Query("api_key") apiKey: String = "3baf383589d324b2f4fe8fbdbf10275f"
    ): Call<GetFilmResponse>
}