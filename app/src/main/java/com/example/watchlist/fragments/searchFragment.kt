package com.example.watchlist.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
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
import com.example.watchlist.adapters.SearchResultsAdapter
import com.example.watchlist.data.Film
import com.example.watchlist.data.FilmsRepository


class SearchFragment:Fragment() {

    private lateinit var searchEditText: EditText
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var searchResultsAdapter: SearchResultsAdapter
    private lateinit var searchResultsLayoutMgr: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        searchEditText = view.findViewById(R.id.searchEditText)

        // Set up RecyclerView
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView)
        searchResultsLayoutMgr = GridLayoutManager(
            requireContext(),
            3,
            GridLayoutManager.VERTICAL,
            false
        )
        searchResultsRecyclerView.layoutManager = searchResultsLayoutMgr
        searchResultsAdapter = SearchResultsAdapter(mutableListOf()) { film -> showFilmDetails(film) }
        searchResultsRecyclerView.adapter = searchResultsAdapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Not used in this example
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used in this example
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }
        })

        return view
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

    private fun performSearch(query: String) {
        FilmsRepository.getFilmSearch(
            query,
            ::onFilmSearched,
            ::onError
        )
    }

    private fun onFilmSearched(films: List<Film>){
        searchResultsAdapter.clearFilms()
        searchResultsAdapter.appendFilms(films)
        Log.d("searchResults", films.toString())
    }


    private fun onError() {
        Toast.makeText(requireContext(), getString(R.string.error_fetch_films), Toast.LENGTH_SHORT).show()
    }
}