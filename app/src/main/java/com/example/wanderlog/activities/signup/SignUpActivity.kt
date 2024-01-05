package com.example.wanderlog.activities.signup
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wanderlog.MainActivity
import com.example.wanderlog.R
import com.example.wanderlog.activities.login.LoginActivity
import com.example.wanderlog.api.service.UserService
import com.example.wanderlog.database.models.User
import com.example.wanderlog.retrofit.RetrofitInstance
import com.example.wanderlog.utils.EmailUtils.validateEmail
import com.example.wanderlog.utils.PasswordUtils.validatePassword
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import java.util.Optional


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

            if(!validateEmail(emailInput.text.toString())){
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!validatePassword(passwordInput.text.toString())){
                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(
                email = emailInput.text.toString(),
                password = passwordInput.text.toString(),
                trips = setOf()
            )
            val userService: UserService = RetrofitInstance.getRetrofitInstance().create(
                UserService::class.java
            )
            val call: Call<Optional<User>> = userService.saveUser(user)
            call.enqueue(object : retrofit2.Callback<Optional<User>> {
                override fun onResponse(
                    call: Call<Optional<User>>,
                    response: retrofit2.Response<Optional<User>>
                ) {
                    if (response.isSuccessful) {
                        val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Error creating user",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Optional<User>>, t: Throwable) {
                    Toast.makeText(
                        this@SignUpActivity,
                        t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

}