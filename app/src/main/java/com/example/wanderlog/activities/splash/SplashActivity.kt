package com.example.wanderlog.activities.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.wanderlog.R
import com.example.wanderlog.activities.signup.SignUpActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var btnGetStarted: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        btnGetStarted = findViewById(R.id.btn_get_started)

        goToMainActivity()
    }

    private fun goToMainActivity() {
        btnGetStarted?.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}