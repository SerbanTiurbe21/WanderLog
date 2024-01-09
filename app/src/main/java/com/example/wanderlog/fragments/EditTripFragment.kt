package com.example.wanderlog.fragments

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.wanderlog.MainActivity
import com.example.wanderlog.R
import com.example.wanderlog.api.service.TripService
import com.example.wanderlog.database.dto.TripDTO
import com.example.wanderlog.database.dto.UserDTO
import com.example.wanderlog.database.models.Trip
import com.example.wanderlog.retrofit.RetrofitInstance
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileNotFoundException

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EditTripFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var tripId: String? = null
    private var currentRating: Float = 0f
    private var selectedImageUri: Uri? = null
    private var initialUri: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private var tripType: String? = null
    private var isFavourite: Boolean = false
    private var updatedPhotoUri: String? = null

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
        val view: View = inflater.inflate(R.layout.fragment_edit_trip, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        loadTripDetails(tripId)

        val updateButton = view.findViewById<TextView>(R.id.btnUpdateUploadPhoto)
        val btnUpdateTrip = view.findViewById<TextView>(R.id.btnUpdateTrip)


        view.findViewById<RatingBar>(R.id.updateRatingBar).setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            currentRating = rating
            view.findViewById<TextView>(R.id.ratingBarText).text = rating.toString()
        }



        updateButton.setOnClickListener{
            selectImageLauncher.launch(arrayOf("image/*"))
        }

        btnUpdateTrip.setOnClickListener{
            val currentUser = getCurrentUserFromPreferences()
            val userId = currentUser?.id ?: ""
            val updatedFrom = view.findViewById<TextInputEditText>(R.id.fromUpdateTextInputEditText).text.toString()
            val updatedTo = view.findViewById<TextInputEditText>(R.id.toUpdateTextInputEditText).text.toString()
            val tripName = view.findViewById<TextView>(R.id.textViewUpdateTripName).text.toString()
            val updatedMinPrice = view.findViewById<TextInputEditText>(R.id.minUpdatePriceEditText).text.toString()
            val updatedMaxPrice = view.findViewById<TextInputEditText>(R.id.maxUpdatePriceEditText).text.toString()
            val updatedRating = currentRating

            updatedPhotoUri = if(selectedImageUri != null){
                selectedImageUri.toString()
            } else {
                initialUri.toString()
            }


            if(validateTripFields(tripName, updatedFrom, updatedTo, updatedMinPrice, updatedMaxPrice, updatedRating,
                    updatedPhotoUri!!
                )){
                val trip = TripDTO(
                    userId,
                    tripName,
                    startDate!!,
                    endDate!!,
                    updatedFrom,
                    updatedTo,
                    tripType!!,
                    (updatedMinPrice.toFloat() + updatedMaxPrice.toFloat()) / 2,
                    updatedRating,
                    updatedPhotoUri!!,
                    0f,
                    isFavourite
                )
                updateTrip(trip)
            }
        }
        return view
    }

    private fun updateTrip(trip: TripDTO) {
        val tripService: TripService =
            RetrofitInstance.getRetrofitInstance().create(TripService::class.java)
        val call: Call<TripDTO?> = tripService.updateTripDTOById(tripId!!, trip)
        call.enqueue(object : Callback<TripDTO?> {
            override fun onResponse(call: Call<TripDTO?>, response: Response<TripDTO?>) {
                if (response.isSuccessful) {
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.e("EditTripFragment", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<TripDTO?>, t: Throwable) {
                Log.e("EditTripFragment", "Failure: ${t.message}")
            }
        })
    }

    private fun validateTripFields(
        tripName: String,
        updatedFrom: String,
        updatedTo: String,
        updatedMinPrice: String,
        updatedMaxPrice: String,
        updatedRating: Float,
        updatedPhotoUri: String
    ): Boolean {
        if(tripName.isEmpty()){
            Toast.makeText(context, "Trip name cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if(updatedFrom.isEmpty()){
            Toast.makeText(context, "From cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if(updatedTo.isEmpty()){
            Toast.makeText(context, "To cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if(updatedMinPrice.isEmpty()){
            Toast.makeText(context, "Min price cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if(updatedMaxPrice.isEmpty()){
            Toast.makeText(context, "Max price cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if(updatedRating == 0f){
            Toast.makeText(context, "Rating cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if(updatedPhotoUri.isEmpty()){
            Toast.makeText(context, "Photo cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditTripFragment().apply {
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
                            view?.findViewById<ImageView>(R.id.ivTripImage)?.loadImageAsync(
                                requireContext(),
                                trip.photoUri
                            )
                            view?.findViewById<TextView>(R.id.textViewUpdateTripName)?.text = trip.tripName
                            view?.findViewById<RatingBar>(R.id.updateRatingBar)?.rating = trip.rating
                            view?.findViewById<TextView>(R.id.ratingBarText)?.text = trip.rating.toString()
                            view?.findViewById<TextInputEditText>(R.id.fromUpdateTextInputEditText)?.setText(trip.origin)
                            view?.findViewById<TextInputEditText>(R.id.toUpdateTextInputEditText)?.setText(trip.destination)
                            startDate = trip.startDate
                            endDate = trip.endDate
                            tripType = trip.tripType
                            isFavourite = trip.isFavourite
                            initialUri = trip.photoUri
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

    private var selectImageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            try {
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context?.contentResolver?.takePersistableUriPermission(uri, takeFlags)
                selectedImageUri = uri
            } catch (e: SecurityException) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to get permission for the selected file.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentUserFromPreferences(): UserDTO? {
        val sharedPreferences = requireActivity().getSharedPreferences("sharedPrefs",
            Context.MODE_PRIVATE
        )
        val userJson = sharedPreferences.getString("USER", null) ?: return null

        return try {
            Gson().fromJson(userJson, UserDTO::class.java)
        } catch (e: JsonSyntaxException) {
            Log.e("AddTripFragment", "Error parsing user JSON", e)
            null
        }
    }
}