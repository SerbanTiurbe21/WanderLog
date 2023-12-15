package com.example.wanderlog.activities.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.wanderlog.MainActivity
import com.example.wanderlog.R
import com.example.wanderlog.activities.login.LoginActivity
import com.example.wanderlog.utils.EmailUtils
import com.example.wanderlog.utils.PasswordUtils
import com.google.android.material.textfield.TextInputEditText

class SignUpActivity : AppCompatActivity() {
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var ivInstagram: ImageView
    private lateinit var ivFacebook: ImageView
    private lateinit var ivGoogle: ImageView
    private lateinit var tvLogin: TextView
    private lateinit var tvSkip: TextView
    private lateinit var btnGetStarted: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setup()
        onTvLoginClicked()
        onTvSkipClicked()
        onBtnGetStartedClicked()
    }

    private fun setup(){
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        ivInstagram = findViewById(R.id.iv_instagram)
        ivFacebook = findViewById(R.id.iv_facebook)
        ivGoogle = findViewById(R.id.iv_google)
        tvLogin = findViewById(R.id.tv_login)
        tvSkip = findViewById(R.id.tv_skip)
        btnGetStarted = findViewById(R.id.btn_get_started)
    }

    private fun onTvLoginClicked(){
        tvLogin.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onTvSkipClicked(){
        tvSkip.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onBtnGetStartedClicked() {
        btnGetStarted.setOnClickListener {
            if(emailInput.text.toString().isEmpty() && passwordInput.text.toString().isEmpty()) {
                Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(emailInput.text.toString().isEmpty()){
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(passwordInput.text.toString().isEmpty()){
                Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!EmailUtils.validateEmail(emailInput.text.toString())){
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!PasswordUtils.validatePassword(passwordInput.text.toString())){
                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Add user to database

            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}