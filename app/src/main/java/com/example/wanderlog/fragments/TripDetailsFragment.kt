package com.example.wanderlog.fragments

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.wanderlog.R
import com.example.wanderlog.api.service.TripService
import com.example.wanderlog.api.weatherapi.Example
import com.example.wanderlog.api.weatherapi.Main
import com.example.wanderlog.api.weatherapi.WeatherApi
import com.example.wanderlog.database.models.Trip
import com.example.wanderlog.retrofit.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.FileNotFoundException


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class TripDetailsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var tripId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            tripId = it.getString("tripId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_trip_details, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        loadTripDetails(tripId)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TripDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun loadTripDetails(tripId: String?) {
        tripId?.let {
            val tripService: TripService =
                RetrofitInstance.getRetrofitInstance().create(TripService::class.java)
            val call: Call<Trip?> = tripService.getTripById(tripId)
            call.enqueue(object : Callback<Trip?> {
                override fun onResponse(call: Call<Trip?>, response: Response<Trip?>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            val trip = it
                            view?.findViewById<TextView>(R.id.tvDepartureCity)?.text = trip.origin
                            view?.findViewById<TextView>(R.id.tvArrivalCity)?.text = trip.destination
                            view?.findViewById<TextView>(R.id.origin3)?.text = trip.origin.take(3)
                            view?.findViewById<TextView>(R.id.destination3)?.text = trip.destination.take(3)
                            view?.findViewById<TextView>(R.id.tvDepartureDate)?.text = trip.startDate
                            view?.findViewById<TextView>(R.id.tvArrivalDate)?.text = trip.endDate
                            view?.findViewById<TextView>(R.id.tvDepartureStation)?.text = trip.origin
                            view?.findViewById<TextView>(R.id.tvArrivalStation)?.text = trip.destination
                            view?.findViewById<TextView>(R.id.tvPrice)?.text = buildString { append(trip.price.toString().split(".")[0])
                                append("€") }
                            view?.findViewById<ImageView>(R.id.ivTripImage)?.loadImageAsync(requireContext(), trip.photoUri)
                            updateWeatherInfo(trip.destination)
                        }
                    } else {
                        Log.e("TripDetailsFragment", "Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Trip?>, t: Throwable) {
                    Log.e("TripDetailsFragment", "Failure: ${t.message}")
                }
            })
        }
    }

    private fun updateWeatherInfo(cityName: String) {
        val apiKey = "8f0bd97aaf59233622f5cba4c4c86397"
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val weatherApi = retrofit.create(WeatherApi::class.java)

        weatherApi.getWeather(cityName, apiKey)?.enqueue(object : Callback<Example?> {
            override fun onResponse(call: Call<Example?>, response: Response<Example?>) {
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    val mainWeather = weatherData?.main
                    updateWeatherViews(mainWeather)
                } else {
                    Log.e("WeatherApi", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Example?>, t: Throwable) {
                Log.e("WeatherApi", "Failure: ${t.message}")
            }
        })
    }

    private fun updateWeatherViews(mainWeather: Main?) {
        mainWeather?.let {
            val tempInCelsius = it.temp?.minus(273.15) ?: 0.0
            val feelsLikeInCelsius = it.feelsLike?.minus(273.15) ?: 0.0
            val humidity = it.humidity ?: 0

            view?.findViewById<TextView>(R.id.tvWeather)?.text = getString(R.string.temperature, tempInCelsius)
            view?.findViewById<TextView>(R.id.tvFeelsLike)?.text = getString(R.string.feels_like, feelsLikeInCelsius)
            view?.findViewById<TextView>(R.id.tvHumidity)?.text = getString(R.string.humidity, humidity)
        }
    }

    private fun ImageView.loadImageAsync(context: Context, imageUriString: String) {
        val imageView = this
        CoroutineScope(Dispatchers.IO).launch {
            val contentUri = Uri.parse(imageUriString)

            val bitmap = try {
                val imageStream = context.contentResolver.openInputStream(contentUri)
                BitmapFactory.decodeStream(imageStream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                null
            }

            withContext(Dispatchers.Main) {
                bitmap?.let {
                    imageView.setImageBitmap(it)
                }
            }
        }
    }
}