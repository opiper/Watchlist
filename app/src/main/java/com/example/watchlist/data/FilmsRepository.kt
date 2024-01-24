package com.example.watchlist.data

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.reflect.KFunction1

object FilmsRepository {

    private val api: Api

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(Api::class.java)
    }

    fun getPopularFilms(
        page: Int = 1,
        onSuccess: (films: List<Film>) -> Unit,
        onError: () -> Unit
    ) {
        api.getPopularFilms(page = page)
            .enqueue(object : Callback<GetFilmResponse> {
                override fun onResponse(
                    call: Call<GetFilmResponse>,
                    response: Response<GetFilmResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            onSuccess.invoke(responseBody.movies)
                        } else {
                            onError.invoke()
                        }
                    }
                }

                override fun onFailure(call: Call<GetFilmResponse>, t: Throwable) {
                    onError.invoke()
                }
            })
    }

    fun getTopRatedFilms(
        page: Int = 1,
        onSuccess: (films: List<Film>) -> Unit,
        onError: () -> Unit
    ) {
        api.getTopRatedFilms(page = page)
            .enqueue(object : Callback<GetFilmResponse> {
                override fun onResponse(
                    call: Call<GetFilmResponse>,
                    response: Response<GetFilmResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            onSuccess.invoke(responseBody.movies)
                        } else {
                            onError.invoke()
                        }
                    }
                }

                override fun onFailure(call: Call<GetFilmResponse>, t: Throwable) {
                    onError.invoke()
                }
            })
    }

    fun getFilmDetails(
        filmId: Long,
        onSuccess: (film: Film) -> Unit,
        onError: () -> Unit
    ) {
        api.getFilmDetails(filmId)
            .enqueue(object: Callback<Film> {
                override fun onResponse(call: Call<Film>, response: Response<Film>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            onSuccess.invoke(responseBody)
                        } else {
                            onError.invoke()
                        }
                    }
                }

                override fun onFailure(call: Call<Film>, t: Throwable) {
                    onError.invoke()
                }
            })
    }

    fun getFilmSearch(
        query: String,
        onSuccess: (films: List<Film>) -> Unit,
        onError: () -> Unit
    ) {
        api.getFilmSearch(query = query)
            .enqueue(object : Callback<GetFilmResponse> {
                override fun onResponse(
                    call: Call<GetFilmResponse>,
                    response: Response<GetFilmResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            onSuccess.invoke(responseBody.movies)
                        } else {
                            onError.invoke()
                        }
                    }
                }

                override fun onFailure(call: Call<GetFilmResponse>, t: Throwable) {
                    onError.invoke()
                }
            })
    }
}