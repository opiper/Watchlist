package com.example.watchlist.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.watchlist.R
import com.example.watchlist.data.Film

class WatchlistFilmAdapter(
    private var films: MutableList<Film>,
    private val onMouseClick: (film: Film) -> Unit
) : RecyclerView.Adapter<WatchlistFilmAdapter.FilmViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilmViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_film_watchlist, parent, false)
        return FilmViewHolder(view)
    }

    override fun getItemCount(): Int = films.size

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        holder.bind(films[position])
    }

    fun appendFilms(films: List<Film>) {
        this.films.clear()
        this.films.addAll(films)
        notifyItemRangeInserted(
            this.films.size,
            films.size
        )
    }

    inner class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val poster: ImageView = itemView.findViewById(R.id.item_film_poster)
        private val title: TextView = itemView.findViewById(R.id.item_film_title)
        private val releaseDate: TextView = itemView.findViewById(R.id.item_film_release_date)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.film_rating)

        fun bind(film: Film) {
            Log.d("adapter", film.toString())
            Glide.with(itemView)
                .load("https://image.tmdb.org/t/p/w342${film.posterPath}")
                .transform(CenterCrop())
                .into(poster)

            title.text = film.title
            releaseDate.text = film.releaseDate
            ratingBar.rating = film.rating / 2

            itemView.setOnClickListener { onMouseClick.invoke(film) }
        }
    }
}