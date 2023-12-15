package com.example.wanderlog.activities.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.wanderlog.MainActivity
import com.example.wanderlog.R
import com.example.wanderlog.activities.forgotpassword.ForgotPasswordActivity
import com.example.wanderlog.utils.EmailUtils.validateEmail
import com.example.wanderlog.utils.PasswordUtils.validatePassword
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInputLogIn: TextInputEditText
    private lateinit var passwordInputLogIn: TextInputEditText
    private lateinit var btnGetStartedLogIn: Button
    private lateinit var tvForgotPasswordLogIn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setup()
        onBtnGetStartedClicked()
        onTvForgotPasswordClicked()
    }

    private fun setup(){
        emailInputLogIn = findViewById(R.id.email_input_log_in)
        passwordInputLogIn = findViewById(R.id.password_input_log_in)
        btnGetStartedLogIn = findViewById(R.id.btn_get_started_log_in)
        tvForgotPasswordLogIn = findViewById(R.id.tv_forgot_password_log_in)
    }

    private fun onBtnGetStartedClicked(){
        btnGetStartedLogIn.setOnClickListener {

            if(emailInputLogIn.text.toString().isEmpty() && passwordInputLogIn.text.toString().isEmpty()){
                Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(emailInputLogIn.text.toString().isEmpty()){
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(passwordInputLogIn.text.toString().isEmpty()){
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!validateEmail(emailInputLogIn.text.toString())){
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!validatePassword(passwordInputLogIn.text.toString())){
                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Check in the backend if the user exists

            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onTvForgotPasswordClicked(){
        tvForgotPasswordLogIn.setOnClickListener {
            var intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}