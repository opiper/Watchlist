package com.example.watchlist.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watchlist.R
import com.example.watchlist.activities.FILM_BACKDROP
import com.example.watchlist.activities.FILM_ID
import com.example.watchlist.activities.FILM_OVERVIEW
import com.example.watchlist.activities.FILM_POSTER
import com.example.watchlist.activities.FILM_RATING
import com.example.watchlist.activities.FILM_RELEASE_DATE
import com.example.watchlist.activities.FILM_TITLE
import com.example.watchlist.activities.FilmDetailsActivity
import com.example.watchlist.adapters.FilmsAdapter
import com.example.watchlist.data.Film
import com.example.watchlist.data.FilmsRepository

class FilmsFragment:Fragment() {

    private lateinit var popularFilms: RecyclerView
    private lateinit var popularFilmsAdapter: FilmsAdapter
    private lateinit var popularFilmsLayoutMgr: GridLayoutManager

    private var popularFilmsPage = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_films, container, false)

        // Popular Films Initial Grab
        popularFilms = view.findViewById(R.id.popularFilms)
        popularFilmsLayoutMgr = GridLayoutManager(
            requireContext(),
            3,
            GridLayoutManager.VERTICAL,
            false
        )
        popularFilms.layoutManager = popularFilmsLayoutMgr
        popularFilmsAdapter = FilmsAdapter(mutableListOf()) { film -> showFilmDetails(film) }
        popularFilms.adapter = popularFilmsAdapter

        getPopularFilms()

        return view
    }

    private fun getPopularFilms() {
        FilmsRepository.getPopularFilms(
            popularFilmsPage,
            ::onPopularFilmsFetched,
            ::onError
        )
    }

    private fun attachPopularFilmsOnScrollListener() {
        popularFilms.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = popularFilmsLayoutMgr.itemCount
                val visibleItemCount = popularFilmsLayoutMgr.childCount
                val firstVisibleItem = popularFilmsLayoutMgr.findFirstVisibleItemPosition()

                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    popularFilms.removeOnScrollListener(this)
                    popularFilmsPage++
                    getPopularFilms()
                }
            }
        })
    }

    private fun onPopularFilmsFetched(films: List<Film>){
        popularFilmsAdapter.appendFilms(films)
        attachPopularFilmsOnScrollListener()
    }

    private fun showFilmDetails(film: Film) {
        val intent = Intent(requireContext(), FilmDetailsActivity::class.java)
        intent.putExtra(FILM_BACKDROP, film.backdropPath)
        intent.putExtra(FILM_POSTER, film.posterPath)
        intent.putExtra(FILM_TITLE, film.title)
        intent.putExtra(FILM_RATING, film.rating)
        intent.putExtra(FILM_RELEASE_DATE, film.releaseDate)
        intent.putExtra(FILM_OVERVIEW, film.overview)
        intent.putExtra(FILM_ID, film.id)
        startActivity(intent)
    }

    private fun onError() {
        Toast.makeText(requireContext(), getString(R.string.error_fetch_films), Toast.LENGTH_SHORT).show()
    }
}
