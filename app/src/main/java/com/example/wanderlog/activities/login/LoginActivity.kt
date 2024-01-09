package com.example.wanderlog.activities.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.example.wanderlog.MainActivity
import com.example.wanderlog.R
import com.example.wanderlog.activities.forgotpassword.ForgotPasswordActivity
import com.example.wanderlog.api.service.UserService
import com.example.wanderlog.database.dto.LoginRequest
import com.example.wanderlog.database.dto.UserDTO
import com.example.wanderlog.database.models.Trip
import com.example.wanderlog.retrofit.RetrofitInstance
import com.example.wanderlog.utils.EmailUtils.validateEmail
import com.example.wanderlog.utils.PasswordUtils.validatePassword
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInputLogIn: TextInputEditText
    private lateinit var passwordInputLogIn: TextInputEditText
    private lateinit var btnGetStartedLogIn: Button
    private lateinit var tvForgotPasswordLogIn: TextView
    private lateinit var chkTermsConditions: CheckBox
    private val userService: UserService = RetrofitInstance.getRetrofitInstance().create(
        UserService::class.java
    )

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
        chkTermsConditions = findViewById(R.id.chk_terms_conditions)
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

            if(!chkTermsConditions.isChecked){
                Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginRequest = LoginRequest(
                emailInputLogIn.text.toString(),
                passwordInputLogIn.text.toString()
            )

            val call = userService.login(loginRequest)
            call.enqueue(object : retrofit2.Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if (response.isSuccessful) {
                        if (response.body() == true) {
                            Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                            getCurrentUserAndSaveToPreferences()
                            var intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun onTvForgotPasswordClicked(){
        tvForgotPasswordLogIn.setOnClickListener {
            var intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getCurrentUserAndSaveToPreferences() {
        val apiCall: Call<ResponseBody> = userService.getUserByEmail(emailInputLogIn.text.toString())
        apiCall.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.string()?.let { responseBodyString ->
                        val jsonObject = JSONObject(responseBodyString)
                        val userId: String = jsonObject.getString("id")
                        val userEmail: String = jsonObject.getString("email")
                        val userPassword: String = jsonObject.getString("password")
                        val type = object : TypeToken<Set<Trip>>() {}.type
                        val userTrips: Set<Trip> = Gson().fromJson(jsonObject.get("trips").toString(), type)
                        val userDTO = UserDTO(userId, userEmail, userPassword, userTrips)

                        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
                        sharedPreferences.edit().apply {
                            putString("USER", Gson().toJson(userDTO))
                            apply()
                        }
                    } ?: run {
                        Log.e("GetCurrentUser", "Response body is null or empty")
                    }
                } else {
                    Log.e("GetCurrentUser", "Response not successful")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("GetCurrentUser", "Network error: ${t.localizedMessage}")
            }
        })
    }
}