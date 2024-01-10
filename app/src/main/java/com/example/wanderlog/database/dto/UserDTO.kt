package com.example.wanderlog.database.dto

import com.example.wanderlog.database.models.Trip

data class UserDTO(
    val id: String,
    val email: String,
    val password: String,
    var trips: Set<Trip>
)