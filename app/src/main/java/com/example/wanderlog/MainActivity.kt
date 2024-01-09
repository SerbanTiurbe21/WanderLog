package com.example.wanderlog

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.wanderlog.database.dto.UserDTO
import com.example.wanderlog.database.models.Trip
import com.example.wanderlog.databinding.ActivityMainBinding
import com.example.wanderlog.utils.LoyaltyLevel
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var ratingTextView: TextView
    private lateinit var textViewLevel: TextView
    private lateinit var textViewLoyaltyProgram: TextView
    private lateinit var imageViewFirstLevel: ImageView
    private lateinit var imageViewSecondLevel: ImageView
    private lateinit var imageViewThirdLevel: ImageView
    private lateinit var imageViewFourthLevel: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_aboutUs, R.id.nav_contact, R.id.nav_share
            ), drawerLayout
        )

        val headerView = navView.getHeaderView(0)
        userNameTextView = headerView.findViewById(R.id.textViewName)
        userEmailTextView = headerView.findViewById(R.id.textViewEmail)
        ratingTextView = headerView.findViewById(R.id.ratingTextView)
        textViewLevel = headerView.findViewById(R.id.textViewLevel)
        textViewLoyaltyProgram = headerView.findViewById(R.id.textViewLoyaltyProgram)
        imageViewFirstLevel = headerView.findViewById(R.id.imageViewFirstLevel)
        imageViewSecondLevel = headerView.findViewById(R.id.imageViewSecondLevel)
        imageViewThirdLevel = headerView.findViewById(R.id.imageViewThirdLevel)
        imageViewFourthLevel = headerView.findViewById(R.id.imageViewFourthLevel)

        val currentUser: UserDTO? = retrieveCurrentUser()
        if (currentUser != null) {
            val remainingTrips: Int = when(currentUser.trips.size){
                in 0..4 -> 5 - currentUser.trips.size
                in 5..9 -> 10 - currentUser.trips.size
                in 10..14 -> 15 - currentUser.trips.size
                else -> 0
            }
            updateUI(extractNameFromEmail(currentUser.email), currentUser.email, calculateTripsRating(currentUser.trips), getUserLevel(currentUser.trips.size), remainingTrips)
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun updateUI(name: String, email: String, rating: String, level: String, remainingTrips: Int) {
        userNameTextView.text = name
        userEmailTextView.text = email
        ratingTextView.text = rating
        textViewLevel.text = level

        val nextLevel: String = when(level){
            "Bronze Level" -> "Silver Level"
            "Silver Level" -> "Gold Level"
            "Gold Level" -> "Platinum Level"
            else -> "Platinum Level"
        }

        val loyaltyText = getString(R.string.loyalty_program_text, name, level, remainingTrips, "31.12.2024", nextLevel)
        val spannableText = SpannableStringBuilder(loyaltyText)

        fun setBoldSpan(text: String) {
            val start = loyaltyText.indexOf(text)
            if (start != -1) {
                spannableText.setSpan(
                    StyleSpan(Typeface.BOLD),
                    start,
                    start + text.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        setBoldSpan(name)
        setBoldSpan(level)
        setBoldSpan(nextLevel)

        textViewLoyaltyProgram.text = spannableText

        val enumLevel = when(level){
            "Bronze Level" -> "BRONZE"
            "Silver Level" -> "SILVER"
            "Gold Level" -> "GOLD"
            else -> "PLATINUM"
        }
        updateLoyaltyIcons(LoyaltyLevel.valueOf(enumLevel))
    }

    private fun extractNameFromEmail(email: String): String {
        val usernamePart = email.substringBefore("@")
        return usernamePart.uppercase()
    }

    private fun retrieveCurrentUser(): UserDTO? {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val userJson = sharedPreferences.getString("USER", null)
        return if (userJson != null) {
            Gson().fromJson(userJson, UserDTO::class.java)
        } else {
            null
        }
    }

    private fun calculateTripsRating(trips: Set<Trip>): String {
        if (trips.isEmpty()) {
            return "0.0"
        }
        var rating = 0.0
        for (trip in trips) {
            rating += trip.rating
        }
        return (rating / trips.size).toString()
    }

    private fun getUserLevel(tripCount: Int): String {
        return when {
            tripCount >= 15 -> "Platinum Level"
            tripCount >= 10 -> "Gold Level"
            tripCount >= 5-> "Silver Level"
            else -> "Bronze Level"
        }
    }

    private fun updateLoyaltyIcons(currentLevel: LoyaltyLevel) {
        when (currentLevel) {
            LoyaltyLevel.BRONZE -> {
                imageViewFirstLevel.setImageResource(R.drawable.photoicon)
                imageViewSecondLevel.setImageResource(R.drawable.secondlevel)
                imageViewThirdLevel.setImageResource(R.drawable.lockedlevel)
                imageViewFourthLevel.setImageResource(R.drawable.lockedlevel)
            }
            LoyaltyLevel.SILVER -> {
                imageViewFirstLevel.setImageResource(R.drawable.checkedicon)
                imageViewSecondLevel.setImageResource(R.drawable.photoicon)
                imageViewThirdLevel.setImageResource(R.drawable.thirdlevelicon)
                imageViewFourthLevel.setImageResource(R.drawable.fourthlevel)
            }
            LoyaltyLevel.GOLD -> {
                imageViewFirstLevel.setImageResource(R.drawable.checkedicon)
                imageViewSecondLevel.setImageResource(R.drawable.checkedicon)
                imageViewThirdLevel.setImageResource(R.drawable.photoicon)
                imageViewFourthLevel.setImageResource(R.drawable.fourthlevel)
            }
            LoyaltyLevel.PLATINUM -> {
                imageViewFirstLevel.setImageResource(R.drawable.checkedicon)
                imageViewSecondLevel.setImageResource(R.drawable.checkedicon)
                imageViewThirdLevel.setImageResource(R.drawable.checkedicon)
                imageViewFourthLevel.setImageResource(R.drawable.photoicon)
            }
        }
    }
}