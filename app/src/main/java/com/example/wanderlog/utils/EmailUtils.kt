package com.example.wanderlog.utils

object EmailUtils {
    fun validateEmail(target: String): Boolean {
        return target.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}