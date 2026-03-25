package com.example.first_exercise

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.first_exercise.model.StudySession
import com.example.first_exercise.viewmodel.SessionEditViewModel
//Uses Google Play Services Location API to retrieve the user’s location
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.text.Editable
import android.text.TextWatcher

class EditSessionActivity : AppCompatActivity() {

    private val vm: SessionEditViewModel by viewModels()
    private var courseId: String? = null
    private var courseName: String? = null
    //save GPS
    private var selectedLat: Double? = null
    private var selectedLon: Double? = null
    //Address from API
    private var selectedAddress: String = ""
    //Source of the location: "current" → GPS ,"manual" → API
    private var locationSource: String = ""

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Link to login XML
        setContentView(R.layout.activity_edit_session)

        val etTopic: EditText = findViewById(R.id.etTopic)
        val etDate: EditText = findViewById(R.id.etDate)
        val etTime: EditText = findViewById(R.id.etTime)
        val etManualLocation: EditText = findViewById(R.id.etManualLocation)
        val etZoomUrl: EditText = findViewById(R.id.etZoomUrl)
        val tvSelectedLocation: TextView = findViewById(R.id.tvSelectedLocation)
        val btnUseCurrentLocation: MaterialButton = findViewById(R.id.btnUseCurrentLocation)
        val btnSave: MaterialButton = findViewById(R.id.btnSaveSession)

        courseId = intent.getStringExtra("COURSE_ID")
        courseName = intent.getStringExtra("COURSE_NAME")

        //Date button - opens DatePicker
        etDate.setOnClickListener {
            showDatePicker(etDate)
        }
        //Time button
        etTime.setOnClickListener {
            showTimePicker(etTime)
        }

        //Clicking the search icon searches for the address
        etManualLocation.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableStart = 0
                val drawable = etManualLocation.compoundDrawables[drawableStart]

                if (drawable != null) {
                    val drawableWidth = drawable.bounds.width()
                    if (event.x <= etManualLocation.paddingStart + drawableWidth + 30) {
                        searchManualLocation(etManualLocation, tvSelectedLocation)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
//
//        etManualLocation.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                searchManualLocation(etManualLocation, tvSelectedLocation)
//                true
//            } else {
//                false
//            }
//        }

        etManualLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                selectedLat = null
                selectedLon = null
                selectedAddress = ""
                locationSource = ""
                tvSelectedLocation.text = "No location selected"
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        //GPS button
        btnUseCurrentLocation.setOnClickListener {
            getCurrentLocation(tvSelectedLocation)
        }

        //Save Session button – including validations
        btnSave.setOnClickListener {
            val topic = etTopic.text.toString().trim()
            val date = etDate.text.toString().trim()
            val time = etTime.text.toString().trim()
            val zoomUrl = etZoomUrl.text.toString().trim().ifEmpty { null }

            if (topic.isEmpty()) {
                Toast.makeText(this, "Please enter topic", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (topic.length < 3) {
                Toast.makeText(this, "Topic must be at least 3 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (date.isEmpty()) {
                Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (time.isEmpty()) {
                Toast.makeText(this, "Please select time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isFutureOrToday(date)) {
                Toast.makeText(this, "Please select a future date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (zoomUrl != null && !isValidUrl(zoomUrl)) {
                Toast.makeText(this, "Invalid Zoom link", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedLat == null || selectedLon == null) {
                Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveSession(topic, date, time, zoomUrl)
        }
    }

    /**
     * Sends address to the API
     * Receives lat/lon
     *
     * External API
     */
    private fun searchManualLocation(
        etManualLocation: EditText,
        tvSelectedLocation: TextView
    ) {
        val manualLocation = etManualLocation.text.toString().trim()

        if (manualLocation.isEmpty()) {
            Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show()
            return
        }

        //Geocoding API
        vm.searchLocationByQuery(
            query = manualLocation,
            onSuccess = { lat, lon, address ->
                selectedLat = lat
                selectedLon = lon
                selectedAddress = address
                locationSource = "manual"
                tvSelectedLocation.text = "Selected location: $address"
                Toast.makeText(this, "Location selected", Toast.LENGTH_SHORT).show()
            },
            onError = { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        )
    }

    /**
     * Opens a date dialog
     * Prevents past dates,
     */
    private fun showDatePicker(etDate: EditText) {
        val calendar = Calendar.getInstance()

        val dialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth, 0, 0, 0)
                selectedDate.set(Calendar.MILLISECOND, 0)

                val today = Calendar.getInstance()
                today.set(Calendar.HOUR_OF_DAY, 0)
                today.set(Calendar.MINUTE, 0)
                today.set(Calendar.SECOND, 0)
                today.set(Calendar.MILLISECOND, 0)

                if (selectedDate.before(today)) {
                    Toast.makeText(this, "Please select a future date", Toast.LENGTH_SHORT).show()
                    return@DatePickerDialog
                }

                val formattedDate = String.format(
                    Locale.getDefault(),
                    "%04d-%02d-%02d",
                    year,
                    month + 1,
                    dayOfMonth
                )
                etDate.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dialog.show()
    }

    /**
     * Opens Time
     */
    private fun showTimePicker(etTime: EditText) {
        val calendar = Calendar.getInstance()

        val dialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val formattedTime = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    hourOfDay,
                    minute
                )
                etTime.setText(formattedTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        dialog.show()
    }

    /**
     * converts String → Date
     */
    private fun isFutureOrToday(date: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.isLenient = false

            val selected = sdf.parse(date) ?: return false
            val selectedCal = Calendar.getInstance().apply {
                time = selected
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            !selectedCal.before(today)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Gets the user's current location using GPS (FusedLocationProviderClient),
     * converts it to a readable address via API (reverse geocoding),
     * and updates the UI.
     * Handles permission checks and failure cases.
     */
    private fun getCurrentLocation(tvSelectedLocation: TextView) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    selectedLat = location.latitude
                    selectedLon = location.longitude
                    locationSource = "current"

                    //Reverse Geocoding API
                    vm.reverseGeocode(
                        lat = location.latitude,
                        lon = location.longitude,
                        onSuccess = { address ->
                            selectedAddress = address
                            tvSelectedLocation.text = "Selected location: $selectedAddress"
                        },
                        onError = {
                            selectedAddress = "${location.latitude}, ${location.longitude}"
                            tvSelectedLocation.text = "Selected location: $selectedAddress"
                        }
                    )
                } else {
                    Toast.makeText(this, "Could not get current location", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveSession(
        topic: String,
        date: String,
        time: String,
        zoomUrl: String?
    ) {
        val session = StudySession(
            sessionId = "",
            courseId = courseId ?: "",
            courseName = courseName ?: "",
            topic = topic,
            date = date,
            time = time,
            latitude = selectedLat ?: 0.0,
            longitude = selectedLon ?: 0.0,
            locationAddress = selectedAddress,
            locationSource = locationSource,
            zoomUrl = zoomUrl,
            participantsCount = 0
        )

        vm.saveSession(session) { success ->
            if (success) {
                Toast.makeText(this, "Session saved!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error saving session", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * URL validation
     */
    private fun isValidUrl(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val tvSelectedLocation: TextView = findViewById(R.id.tvSelectedLocation)
                getCurrentLocation(tvSelectedLocation)
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }
}