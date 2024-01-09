package com.example.wanderlog.fragments

import android.app.DatePickerDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.wanderlog.MainActivity
import com.example.wanderlog.R
import com.example.wanderlog.api.service.TripService
import com.example.wanderlog.database.dto.TripDTO
import com.example.wanderlog.database.dto.UserDTO
import com.example.wanderlog.database.models.Trip
import com.example.wanderlog.retrofit.RetrofitInstance
import com.google.android.material.slider.RangeSlider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Call
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddTripFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var selectedDepartureDate: Calendar? = null
    private var selectedImageUri: Uri? = null
    private var currentRating: Float = 0f
    private var selectedTripType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        val view: View = inflater.inflate(R.layout.fragment_add_trip, container, false)
        val departureEditText = view.findViewById<TextInputEditText>(R.id.departureDateTextInputEditText)
        val departureDateLayout = view.findViewById<TextInputLayout>(R.id.departureDateTextInputLayout)
        val arrivingEditText = view.findViewById<TextInputEditText>(R.id.arrivingDateTextInputEditText)
        val arrivingDateLayout = view.findViewById<TextInputLayout>(R.id.arrivingDateTextInputLayout)
        val priceRangeSlider = view.findViewById<RangeSlider>(R.id.priceRangeSlider)
        val minPriceEditText = view.findViewById<EditText>(R.id.minPriceEditText)
        val maxPriceEditText = view.findViewById<EditText>(R.id.maxPriceEditText)
        val btnUploadPhoto = view.findViewById<Button>(R.id.btnUploadPhoto)
        val fromTextInputEditText = view.findViewById<TextInputEditText>(R.id.fromTextInputEditText)
        val toTextInputEditText = view.findViewById<TextInputEditText>(R.id.toTextInputEditText)
        val btnPostTrip = view.findViewById<Button>(R.id.btnAddTrip)

        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)

        ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, _ ->
            currentRating = rating
        }


        departureDateLayout.setEndIconOnClickListener {
            showDatePickerDialog(departureEditText, isDeparture = true)
        }

        departureEditText.setOnClickListener {
            showDatePickerDialog(departureEditText, isDeparture = true)
        }

        arrivingDateLayout.setEndIconOnClickListener {
            showDatePickerDialog(arrivingEditText, isDeparture = false)
        }

        arrivingEditText.setOnClickListener {
            showDatePickerDialog(arrivingEditText, isDeparture = false)
        }

        updateSlider(priceRangeSlider, minPriceEditText, maxPriceEditText)


        btnUploadPhoto.setOnClickListener {
            selectImageLauncher.launch(arrayOf("image/*"))
        }

        val btnCityBreak = view.findViewById<Button>(R.id.btnCityBreak)
        val btnSeaSide = view.findViewById<Button>(R.id.btnSeaSide)
        val btnMountains = view.findViewById<Button>(R.id.btnMountains)
        setupTripTypeSelection(btnCityBreak, btnSeaSide, btnMountains)

        btnPostTrip.setOnClickListener {
            val currentUser = getCurrentUserFromPreferences()
            val userId = currentUser?.id ?: ""
            val tripName = fromTextInputEditText.text.toString() + " to " + toTextInputEditText.text.toString()
            val startDate = departureEditText.text.toString()
            val endDate = arrivingEditText.text.toString()
            val origin = fromTextInputEditText.text.toString()
            val destination = toTextInputEditText.text.toString()
            val tripType = selectedTripType ?: ""
            val price = (priceRangeSlider.values[0] + priceRangeSlider.values[1]) / 2
            val rating = currentRating
            if(selectedImageUri == null){
                Toast.makeText(context, "Please select a photo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val photoUri = selectedImageUri.toString()
            val temperature = 0f
            val isFavourite = false

            if (validateTripFields(tripName, startDate, endDate, origin, destination, priceRangeSlider, rating, photoUri)) {
                val trip = TripDTO(userId, tripName, startDate, endDate, origin, destination, tripType, price, rating, photoUri, temperature, isFavourite)
                postTripToDatabase(trip)
            }
        }

        return view
    }


    private fun validateTripFields(
        tripName: String,
        startDate: String,
        endDate: String,
        origin: String,
        destination: String,
        priceRangeSlider: RangeSlider,
        rating: Float,
        photoUri: String?
    ): Boolean {
        if (tripName.isBlank() || startDate.isBlank() || endDate.isBlank() || origin.isBlank() ||destination.isBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!isValidDateRange(startDate, endDate)) {
            Toast.makeText(context, "Invalid date range", Toast.LENGTH_SHORT).show()
            return false
        }

        val price = (priceRangeSlider.values[0] + priceRangeSlider.values[1]) / 2
        if (price <= 0) {
            Toast.makeText(context, "Price must be greater than 0", Toast.LENGTH_SHORT).show()
            return false
        }

        if (rating < 0 || rating > 5) {
            Toast.makeText(context, "Rating is out of bounds", Toast.LENGTH_SHORT).show()
            return false
        }

        if (photoUri.isNullOrEmpty()) {
            Toast.makeText(context, "Please select a photo", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun isValidDateRange(startDate: String, endDate: String): Boolean {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return try {
            val start = dateFormat.parse(startDate)
            val end = dateFormat.parse(endDate)
            start != null && end != null && !start.after(end)
        } catch (e: ParseException) {
            false
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddTripFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun showDatePickerDialog(editText: TextInputEditText, isDeparture: Boolean) {
        val calendar = Calendar.getInstance()
        if (isDeparture) {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(requireContext(), { _, selectedYear, monthOfYear, dayOfMonth ->
                selectedDepartureDate = Calendar.getInstance().apply {
                    set(selectedYear, monthOfYear, dayOfMonth)
                }
                val selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, selectedYear)
                editText.setText(selectedDate)
            }, year, month, day)

            dpd.show()
        } else {
            selectedDepartureDate?.let { departureDate ->
                val dpd = DatePickerDialog(requireContext(), { _, selectedYear, monthOfYear, dayOfMonth ->
                    val selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, selectedYear)
                    editText.setText(selectedDate)
                }, departureDate.get(Calendar.YEAR), departureDate.get(Calendar.MONTH), departureDate.get(Calendar.DAY_OF_MONTH))

                dpd.datePicker.minDate = departureDate.timeInMillis
                dpd.show()
            }
        }
    }

    private fun updateSlider(
        priceRangeSlider: RangeSlider,
        minPriceEditText: EditText,
        maxPriceEditText: EditText
    ) {
        priceRangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            minPriceEditText.setText(String.format(Locale.US, "%.0f", values[0]))
            maxPriceEditText.setText(String.format(Locale.US, "%.0f", values[1]))
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                minPriceEditText.removeTextChangedListener(this)
                maxPriceEditText.removeTextChangedListener(this)

                s?.toString()?.let { text ->
                    if (text.isNotEmpty()) {
                        try {
                            val parsed = text.toFloat()

                            if (parsed >= priceRangeSlider.values[0] && parsed <= priceRangeSlider.values[1]) {
                                if (minPriceEditText.hasFocus()) {
                                    priceRangeSlider.values = listOf(parsed, priceRangeSlider.values[1])
                                } else if (maxPriceEditText.hasFocus()) {
                                    priceRangeSlider.values = listOf(priceRangeSlider.values[0], parsed)
                                }
                            }
                        } catch (e: NumberFormatException) {
                        }
                    }
                }
                minPriceEditText.addTextChangedListener(this)
                maxPriceEditText.addTextChangedListener(this)
            }
        }
        minPriceEditText.addTextChangedListener(textWatcher)
        maxPriceEditText.addTextChangedListener(textWatcher)
    }

    private fun postTripToDatabase(trip: TripDTO) {
        val tripService: TripService = RetrofitInstance.getRetrofitInstance().create(
            TripService::class.java
        )
        val call: Call<Trip> = tripService.createTrip(trip)
        call.enqueue(object : retrofit2.Callback<Trip> {
            override fun onResponse(call: Call<Trip>, response: retrofit2.Response<Trip>) {
                if (response.isSuccessful) {
                    Log.e("AddTripFragment", response.body().toString())

                    val sharedPreferences = requireActivity().getSharedPreferences("sharedPrefs", MODE_PRIVATE)
                    val userJson = sharedPreferences.getString("USER", null) ?: return
                    val userDTO: UserDTO = Gson().fromJson(userJson, UserDTO::class.java)
                    val updatedTrips = userDTO.trips.toMutableSet()
                    updatedTrips.add(response.body()!!)
                    userDTO.trips = updatedTrips
                    sharedPreferences.edit().apply {
                        putString("USER", Gson().toJson(userDTO))
                        apply()
                    }

                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "Failed to add trip", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Trip>, t: Throwable) {
                Toast.makeText(context, "Failed to add trip", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getCurrentUserFromPreferences(): UserDTO? {
        val sharedPreferences = requireActivity().getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val userJson = sharedPreferences.getString("USER", null) ?: return null

        return try {
            Gson().fromJson(userJson, UserDTO::class.java)
        } catch (e: JsonSyntaxException) {
            Log.e("AddTripFragment", "Error parsing user JSON", e)
            null
        }
    }

    private fun setupTripTypeSelection(btnCityBreak: Button, btnSeaSide: Button, btnMountains: Button) {
        val buttons = listOf(btnCityBreak, btnSeaSide, btnMountains)

        buttons.forEach { button ->
            button.setOnClickListener { it ->
                it.visibility = View.GONE

                val visibleButtons = buttons.filter { it.visibility == View.VISIBLE }
                if (visibleButtons.size == 1) {
                    selectedTripType = visibleButtons.first().text.toString()
                    visibleButtons.first().isEnabled = false
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
}