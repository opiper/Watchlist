package com.example.watchlist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.watchlist.R
import com.example.watchlist.data.Film

class SearchResultsAdapter(
    private var films: MutableList<Film>,
    private val onMouseClick: (film: Film) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_film, parent, false)
        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int = films.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(films[position])
    }

    fun appendFilms(films: List<Film>) {
        this.films.addAll(films)
        notifyItemRangeInserted(
            this.films.size,
            films.size - 1
        )
    }

    fun clearFilms() {
        films.clear()
        notifyDataSetChanged()
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val poster: ImageView = itemView.findViewById(R.id.item_film_poster)

        fun bind(film: Film) {
            Glide.with(itemView)
                .load("https://image.tmdb.org/t/p/w342${film.posterPath}")
                .transform(CenterCrop())
                .into(poster)
            itemView.setOnClickListener { onMouseClick.invoke(film) }
        }
    }
}