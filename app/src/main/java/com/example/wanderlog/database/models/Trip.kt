package com.example.wanderlog.database.models

data class Trip(
    var id: String,
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

) {
    override fun hashCode(): Int {
        return id.hashCode() ?: 0
    }
}

