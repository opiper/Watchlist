package com.example.watchlist.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watchlist.data.FilmsRepository
import com.example.watchlist.R
import com.example.watchlist.activities.FILM_BACKDROP
import com.example.watchlist.activities.FILM_ID
import com.example.watchlist.activities.FILM_OVERVIEW
import com.example.watchlist.activities.FILM_POSTER
import com.example.watchlist.activities.FILM_RATING
import com.example.watchlist.activities.FILM_RELEASE_DATE
import com.example.watchlist.activities.FILM_TITLE
import com.example.watchlist.activities.FilmDetailsActivity
import com.example.watchlist.activities.LoginActivity
import com.example.watchlist.activities.MainActivity
import com.example.watchlist.adapters.FilmsAdapter
import com.example.watchlist.adapters.WatchlistFilmAdapter
import com.example.watchlist.data.Film
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment:Fragment() {

    private lateinit var popularFilms: RecyclerView
    private lateinit var popularFilmsAdapter: FilmsAdapter
    private lateinit var popularFilmsLayoutMgr: LinearLayoutManager

    private lateinit var topRatedFilms: RecyclerView
    private lateinit var topRatedFilmsAdapter: FilmsAdapter
    private lateinit var topRatedFilmsLayoutMgr: LinearLayoutManager

    private var popularFilmsPage = 1
    private var topRatedFilmsPage = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Popular Films Initial Grab
        popularFilms = view.findViewById(R.id.popularFilms)
        popularFilmsLayoutMgr = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        popularFilms.layoutManager = popularFilmsLayoutMgr
        popularFilmsAdapter = FilmsAdapter(mutableListOf()) { film -> showFilmDetails(film) }
        popularFilms.adapter = popularFilmsAdapter

        // Top Rated Films Initial Grab
        topRatedFilms = view.findViewById(R.id.topRatedFilms)
        topRatedFilmsLayoutMgr = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        topRatedFilms.layoutManager = topRatedFilmsLayoutMgr
        topRatedFilmsAdapter = FilmsAdapter(mutableListOf()) { film -> showFilmDetails(film) }
        topRatedFilms.adapter = topRatedFilmsAdapter

        getPopularFilms()
        getTopRatedFilms()

        return view
    }

    private fun getPopularFilms() {
        FilmsRepository.getPopularFilms(
            popularFilmsPage,
            ::onPopularFilmsFetched,
            ::onError
        )
    }

    private fun getTopRatedFilms() {
        FilmsRepository.getTopRatedFilms(
            topRatedFilmsPage,
            ::onTopRatedFilmsFetched,
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

    private fun attachTopRatedFilmsOnScrollListener() {
        topRatedFilms.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = topRatedFilmsLayoutMgr.itemCount
                val visibleItemCount = topRatedFilmsLayoutMgr.childCount
                val firstVisibleItem = topRatedFilmsLayoutMgr.findFirstVisibleItemPosition()

                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    topRatedFilms.removeOnScrollListener(this)
                    topRatedFilmsPage++
                    getTopRatedFilms()
                }
            }
        })
    }

    private fun onPopularFilmsFetched(films: List<Film>){
        popularFilmsAdapter.appendFilms(films)
        attachPopularFilmsOnScrollListener()
    }

    private fun onTopRatedFilmsFetched(films: List<Film>){
        topRatedFilmsAdapter.appendFilms(films)
        attachTopRatedFilmsOnScrollListener()
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
