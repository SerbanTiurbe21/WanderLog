package com.example.wanderlog.api.service

import com.example.wanderlog.database.dto.TripDTO
import com.example.wanderlog.database.models.Trip
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TripService {
    @POST("/api/v1/trips/create")
    fun createTrip(@Body trip: TripDTO): Call<Trip>

    @GET("/api/v1/trips/trip/{id}")
    fun getTripById(@Path("id") id: String): Call<Trip?>

    @GET("/api/v1/trips/user")
    fun getTripsByUserId(@Query("userId") userId: String): Call<ResponseBody>

    @PUT("/api/v1/trips/update/{id}")
    fun updateTripById(@Path("id") id: String, @Body trip: Trip): Call<Trip?>

    @PUT("/api/v1/trips/update/{id}")
    fun updateTripDTOById(@Path("id") id: String, @Body trip: TripDTO): Call<TripDTO?>
}
