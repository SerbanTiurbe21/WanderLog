package com.example.wanderlog.database.models

data class User (
    var email: String,
    var password: String,
    var trips: Set<Trip>
)