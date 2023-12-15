package com.example.wanderlog.activities.forgotpassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.wanderlog.R
import com.example.wanderlog.activities.login.LoginActivity
import com.example.wanderlog.utils.EmailUtils
import com.example.wanderlog.utils.PasswordUtils
import com.google.android.material.textfield.TextInputEditText

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var emailInputForgetPassword: TextInputEditText
    private lateinit var passwordInputForgetPassword: TextInputEditText
    private lateinit var rePasswordInputForgetPassword: TextInputEditText
    private lateinit var btnChangePassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setup()
    }

    private fun setup(){
        emailInputForgetPassword = findViewById(R.id.email_input_forget_password)
        passwordInputForgetPassword = findViewById(R.id.password_input_forget_password)
        rePasswordInputForgetPassword = findViewById(R.id.rePassword_input_forget_password)
        btnChangePassword = findViewById(R.id.btn_change_password)
    }

    private fun onBtnChangePasswordClicked(){
        btnChangePassword.setOnClickListener {
            if(emailInputForgetPassword.text.toString().isEmpty()){
                Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(passwordInputForgetPassword.text.toString().isEmpty()){
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(rePasswordInputForgetPassword.text.toString().isEmpty()){
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!EmailUtils.validateEmail(emailInputForgetPassword.text.toString())){
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!PasswordUtils.validatePassword(passwordInputForgetPassword.text.toString())){
                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!PasswordUtils.validatePassword(rePasswordInputForgetPassword.text.toString())){
                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(passwordInputForgetPassword.text.toString() != rePasswordInputForgetPassword.text.toString()){
                Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(passwordInputForgetPassword.text.toString() == rePasswordInputForgetPassword.text.toString()){
                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()

                // TODO: Update the password in the backend

                // change the activity to the login page
                var intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

        }
    }
}