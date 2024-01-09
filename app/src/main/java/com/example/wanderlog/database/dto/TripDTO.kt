package com.example.wanderlog.database.dto

data class TripDTO(
    var userId: String,
    var tripName: String,
    var startDate: String,
    var endDate: String,
    var origin: String,
    var destination: String,
    var tripType: String,
    var price: Float,
    var rating: Float,
    var photoUri: String,
    var temperature: Float,
    var isFavourite: Boolean
)
