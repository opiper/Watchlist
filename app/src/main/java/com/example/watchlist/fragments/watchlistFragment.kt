package com.example.watchlist.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import com.example.watchlist.adapters.WatchlistFilmAdapter
import com.example.watchlist.data.Film
import com.example.watchlist.data.FilmsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class WatchlistFragment:Fragment(R.layout.fragment_watchlist) {

    private lateinit var wantToWatchRec: RecyclerView
    private lateinit var wantToWatchRecAdapter: WatchlistFilmAdapter
    private lateinit var wantToWatchRecLayoutMgr: LinearLayoutManager

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_watchlist, container, false)

        wantToWatchRec = view.findViewById(R.id.wantToWatchRec)
        wantToWatchRecLayoutMgr = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        wantToWatchRec.layoutManager = wantToWatchRecLayoutMgr
        wantToWatchRecAdapter = WatchlistFilmAdapter(mutableListOf()) { film -> showFilmDetails(film) }
        wantToWatchRec.adapter = wantToWatchRecAdapter

        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            getWatchlistFilms(currentUser.uid, "Films")
        }

        return view
    }

    private fun getWatchlistFilms(userId: String, type: String) {
        db.collection("users")
            .document(userId)
            .collection("watchlist")
            .whereEqualTo("type", type)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Handle the data in querySnapshot.documents
                val filmsInWatchlist = mutableListOf<Long>()

                for (document in querySnapshot.documents) {
                    val filmId = document.getLong("filmId")
                    filmId?.let {
                        filmsInWatchlist.add(it)
                    }
                }

                // Now, you have the list of film IDs in the watchlist
                // You can use this list to fetch detailed information about each film
                fetchWatchlistDetails(filmsInWatchlist)
            }
            .addOnFailureListener {
                // Handle errors
                Toast.makeText(requireContext(), "Failed to retrieve watchlist", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchWatchlistDetails(filmsInWatchlist: List<Long>) {
        val filmsRepository = FilmsRepository

        val fetchedFilms = mutableListOf<Film>()

        var fetchedCount = 0

        for (filmId in filmsInWatchlist) {
            filmsRepository.getFilmDetails(
                filmId,
                { film ->
                    fetchedCount++

                    fetchedFilms.add(film)

                    if (fetchedCount == filmsInWatchlist.size) {
                        onFilmDetailsFetched(fetchedFilms)
                    }
                },
                ::onError
            )
        }
    }

    private fun onFilmDetailsFetched(fetchedFilms: List<Film>) {
        val sortedFilms = fetchedFilms.sortedBy { it.title }
        wantToWatchRecAdapter.appendFilms(sortedFilms)
    }

    private fun onError() {
        Toast.makeText(requireContext(), getString(R.string.error_fetch_films), Toast.LENGTH_SHORT).show()
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
}
