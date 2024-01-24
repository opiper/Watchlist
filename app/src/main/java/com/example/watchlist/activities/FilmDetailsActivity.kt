package com.example.watchlist.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.watchlist.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

const val FILM_BACKDROP = "extra_film_backdrop"
const val FILM_POSTER = "extra_film_poster"
const val FILM_TITLE = "extra_film_title"
const val FILM_RATING = "extra_film_rating"
const val FILM_RELEASE_DATE = "extra_film_release_date"
const val FILM_OVERVIEW = "extra_film_overview"
const val FILM_ID = "extra_film_id"

class FilmDetailsActivity : AppCompatActivity() {

    private lateinit var backdrop: ImageView
    private lateinit var poster: ImageView
    private lateinit var title: TextView
    private lateinit var rating: RatingBar
    private lateinit var releaseDate: TextView
    private lateinit var overview: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_details)

        backdrop = findViewById(R.id.film_backdrop)
        poster = findViewById(R.id.film_poster)
        title = findViewById(R.id.film_title)
        rating = findViewById(R.id.film_rating)
        releaseDate = findViewById(R.id.film_release_date)
        overview = findViewById(R.id.film_overview)

        val extras = intent.extras

        if (extras != null) {
            populateDetails(extras)
        } else {
            finish()
        }

        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val filmId = extras?.getLong(FILM_ID, 0)

        val addToWatchlistButton = findViewById<Button>(R.id.addToWatchlist)
        if (currentUser != null) {
            toggleWatchlistButton(currentUser.uid, filmId, addToWatchlistButton)
        } else {
            addToWatchlistButton.text = "Add to Watchlist"

            addToWatchlistButton.setOnClickListener {
                Toast.makeText(this@FilmDetailsActivity, "Please log in to do this", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleWatchlistButton(userId: String, filmId: Long?, addToWatchlistButton: Button) {
        if (filmId != null) {
            val watchlistRef = db.collection("users")
                .document(userId)
                .collection("watchlist")
                .document(filmId.toString())

            watchlistRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        addToWatchlistButton.text = "Remove from Watchlist"

                        addToWatchlistButton.setOnClickListener {
                            removeFromWatchlist(userId, filmId, addToWatchlistButton)
                        }
                    } else {
                        addToWatchlistButton.text = "Add to Watchlist"

                        addToWatchlistButton.setOnClickListener {
                            addToWatchlist(userId, filmId)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this@FilmDetailsActivity, "Failed to check watchlist", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun removeFromWatchlist(userId: String, filmId: Long?, addToWatchlistButton: Button) {
        if (filmId != null) {
            db.collection("users")
                .document(userId)
                .collection("watchlist")
                .document(filmId.toString())
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this@FilmDetailsActivity, "Removed from watchlist", Toast.LENGTH_SHORT).show()
                    toggleWatchlistButton(userId, filmId, addToWatchlistButton)
                }
                .addOnFailureListener {
                    Toast.makeText(this@FilmDetailsActivity, "Failed to remove from watchlist", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun populateDetails(extras: Bundle) {
        extras.getString(FILM_BACKDROP)?.let { backdropPath ->
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w1280$backdropPath")
                .transform(CenterCrop())
                .into(backdrop)
        }

        extras.getString(FILM_POSTER)?.let { posterPath ->
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w342$posterPath")
                .transform(CenterCrop())
                .into(poster)
        }

        title.text = extras.getString(FILM_TITLE, "")
        rating.rating = extras.getFloat(FILM_RATING, 0f) / 2
        releaseDate.text = extras.getString(FILM_RELEASE_DATE, "")
        overview.text = extras.getString(FILM_OVERVIEW, "")
    }

    private fun addToWatchlist(userId: String, filmId: Long?) {
        val addToWatchlistButton = findViewById<Button>(R.id.addToWatchlist)

        if (filmId != null) {
            val watchlistRef = db.collection("users")
                .document(userId)
                .collection("watchlist")
                .document(filmId.toString())

            val data = hashMapOf(
                "filmId" to filmId,
                "dateAddedToWatchlist" to FieldValue.serverTimestamp(),
                "status" to 1,
                "type" to "Films"
            )

            watchlistRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        Toast.makeText(this@FilmDetailsActivity, "Film is already in the watchlist", Toast.LENGTH_SHORT).show()
                    } else {
                        // Film doesn't exist in the watchlist, add it
                        watchlistRef.set(data)
                            .addOnSuccessListener {
                                Toast.makeText(this@FilmDetailsActivity, "Added to watchlist", Toast.LENGTH_SHORT).show()
                                toggleWatchlistButton(userId, filmId, addToWatchlistButton)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@FilmDetailsActivity, "Failed to add to watchlist", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    // Handle the error
                    Toast.makeText(this@FilmDetailsActivity, "Failed to check watchlist", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
