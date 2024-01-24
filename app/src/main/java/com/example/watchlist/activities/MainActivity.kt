package com.example.watchlist.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.watchlist.R
import com.example.watchlist.fragments.FilmsFragment
import com.example.watchlist.fragments.HomeFragment
import com.example.watchlist.fragments.SearchFragment
import com.example.watchlist.fragments.WatchlistFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        topAppBar = findViewById(R.id.topAppBar)
        navView = findViewById(R.id.navView)
        navView.inflateMenu(R.menu.nav_menu)

        val homeFragment = HomeFragment()
        val filmsFragment = FilmsFragment()
        val watchlistFragment = WatchlistFragment()
        val searchFragment = SearchFragment()

        setCurrentFragment(homeFragment)

        handleAuthenticationStatus()

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setCurrentFragment(homeFragment)
                R.id.film -> setCurrentFragment(filmsFragment)
                R.id.watchlist -> setCurrentFragment(watchlistFragment)
            }
            true
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.burger_menu -> openNavigationMenu()
                R.id.app_bar_search -> setCurrentFragment(searchFragment)
            }
            true
        }

        val loginMenuItem: MenuItem? = navView.menu.findItem(R.id.nav_login)
        loginMenuItem?.setOnMenuItemClickListener {
            openLoginPage()
            true
        }

        val logoutMenuItem: MenuItem? = navView.menu.findItem(R.id.nav_logout)
        logoutMenuItem?.setOnMenuItemClickListener {
            Log.d("Logout", "Works")
            logoutUser()
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }

    private fun openNavigationMenu() {
        // Find the DrawerLayout and NavigationView
        val drawerLayout: DrawerLayout? = findViewById(R.id.drawerLayout)
        val navView: NavigationView? = findViewById(R.id.navView)

        // Check if DrawerLayout and NavigationView are not null
        if (drawerLayout != null && navView != null) {
            // Open the drawer
            drawerLayout.openDrawer(navView)
        }
    }

    private fun openLoginPage() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun logoutUser() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        handleAuthenticationStatus()
    }

    private fun handleAuthenticationStatus() {
        val auth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = auth.currentUser

        if (currentUser != null) {
            updateNavigationMenu(true)
        } else {
            updateNavigationMenu(false)
        }
    }

    private fun updateNavigationMenu(isLoggedIn: Boolean) {
        navView.menu.clear()

        if (isLoggedIn) {
            navView.inflateMenu(R.menu.nav_menu)
        } else {
            navView.inflateMenu(R.menu.nav_menu_anon)
        }
    }
}
