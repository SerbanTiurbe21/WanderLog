package com.example.wanderlog.activities.forgotpassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.wanderlog.R
import com.example.wanderlog.activities.login.LoginActivity
import com.example.wanderlog.api.service.UserService
import com.example.wanderlog.database.models.User
import com.example.wanderlog.retrofit.RetrofitInstance
import com.google.android.material.textfield.TextInputEditText
import com.example.wanderlog.utils.EmailUtils.validateEmail
import com.example.wanderlog.utils.PasswordUtils.validatePassword
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.Optional

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var emailInputForgetPassword: TextInputEditText
    private lateinit var passwordInputForgetPassword: TextInputEditText
    private lateinit var rePasswordInputForgetPassword: TextInputEditText
    private lateinit var btnChangePassword: Button
    private var flag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setup()
        onBtnChangePasswordClicked()
    }

    private fun setup(){
        emailInputForgetPassword = findViewById(R.id.email_input_forget_password)
        passwordInputForgetPassword = findViewById(R.id.password_input_forget_password)
        rePasswordInputForgetPassword = findViewById(R.id.rePassword_input_forget_password)
        btnChangePassword = findViewById(R.id.btn_change_password)
    }

    private fun onBtnChangePasswordClicked(){
        btnChangePassword.setOnClickListener {
            val userService: UserService = RetrofitInstance.getRetrofitInstance().create(
                UserService::class.java
            )

            val apiCall: Call<ResponseBody> = userService.getUserByEmail(emailInputForgetPassword.text.toString())
            apiCall.enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val responseBodyString = response.body()?.string()
                        if (responseBodyString != null) {
                            val jsonObject = JSONObject(responseBodyString)
                            val userPassword: String = jsonObject.getString("password")
                            if (userPassword == passwordInputForgetPassword.text.toString()) {
                                Toast.makeText(this@ForgotPasswordActivity, "New password cannot be the same as the old password.", Toast.LENGTH_SHORT).show()
                                flag = true
                            }
                        } else {
                            Toast.makeText(this@ForgotPasswordActivity, "User not found.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, "Error getting user", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

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

            if(!validateEmail(emailInputForgetPassword.text.toString())){
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!validatePassword(passwordInputForgetPassword.text.toString())){
                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!validatePassword(rePasswordInputForgetPassword.text.toString())){
                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(passwordInputForgetPassword.text.toString() != rePasswordInputForgetPassword.text.toString()){
                Toast.makeText(this, "Passwords does not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if(passwordInputForgetPassword.text.toString() == rePasswordInputForgetPassword.text.toString()){
                val user = User(
                    email = emailInputForgetPassword.text.toString(),
                    password = passwordInputForgetPassword.text.toString(),
                    trips = setOf()
                )

                val call = userService.updateUserByEmail(emailInputForgetPassword.text.toString(), user)
                call.enqueue(object : retrofit2.Callback<Optional<User>> {
                    override fun onResponse(
                        call: Call<Optional<User>>,
                        response: Response<Optional<User>>
                    ) {
                        if (response.isSuccessful) {
                            val user = response.body()
                            if (user != null) {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Password changed successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if(!flag){
                                    var intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                }
                            } else {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Error changing password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }}
                    override fun onFailure(call: Call<Optional<User>>, t: Throwable) {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            t.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }
}