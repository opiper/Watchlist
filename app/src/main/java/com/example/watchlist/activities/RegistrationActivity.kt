package com.example.watchlist.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.watchlist.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Bind Views
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        registerButton = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)

        // Set up click listener for the register button
        registerButton.setOnClickListener {
            // Registration logic
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Show progress bar whilst registering
                progressBar.visibility = ProgressBar.VISIBLE

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        progressBar.visibility = ProgressBar.GONE // Hide progress bar

                        if (task.isSuccessful) {
                            createUserDocument()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Finish the LoginActivity
                        } else {
                            // Registration failed, handle the error
                            Toast.makeText(
                                this,
                                "Registration failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                // Display a message indicating that email and password cannot be empty
                Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createUserDocument() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            db.collection("users")
                .document(currentUser.uid)
                .set(mapOf(
                    "email" to currentUser.email,
                    "name" to "",
                ))
                .addOnSuccessListener {
                    createWatchlistCollection(currentUser.uid)
                }
                .addOnFailureListener {
                    createUserDocument()
                }
        }
    }

    private fun createWatchlistCollection(userId: String) {
        val currentUser = auth.currentUser
        db.collection("users")
            .document(userId)
            .collection("watchlist")
            .document("exampleDocument")
            .set(mapOf("exampleField" to "exampleValue"))
            .addOnSuccessListener {
                Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                if (currentUser != null) {
                    createWatchlistCollection(currentUser.uid)
                }
            }
    }
}