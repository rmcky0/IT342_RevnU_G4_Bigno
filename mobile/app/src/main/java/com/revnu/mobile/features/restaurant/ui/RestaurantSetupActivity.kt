package com.revnu.mobile.features.restaurant.ui

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.revnu.mobile.R
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.core.session.SessionManager
import com.revnu.mobile.features.dashboard.ui.DashboardActivity
import com.revnu.mobile.features.restaurant.model.RestaurantSetupState
import com.revnu.mobile.features.restaurant.repository.RestaurantRepository
import com.revnu.mobile.features.restaurant.viewmodel.RestaurantSetupViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

class RestaurantSetupActivity : AppCompatActivity() {

    private lateinit var etRestaurantName: EditText
    private lateinit var etLocation: EditText
    private lateinit var etOpeningTime: EditText
    private lateinit var etClosingTime: EditText
    private lateinit var btnCompleteSetup: MaterialButton

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: RestaurantSetupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_setup)

        sessionManager = SessionManager(this)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = RestaurantRepository(RetrofitClient.apiService)
                @Suppress("UNCHECKED_CAST")
                return RestaurantSetupViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[RestaurantSetupViewModel::class.java]

        bindViews()
        setupListeners()
        observeViewModel()
    }

    private fun bindViews() {
        etRestaurantName = findViewById(R.id.etRestaurantName)
        etLocation = findViewById(R.id.etLocation)
        etOpeningTime = findViewById(R.id.etOpeningTime)
        etClosingTime = findViewById(R.id.etClosingTime)
        btnCompleteSetup = findViewById(R.id.btnCompleteSetup)
    }

    private fun setupListeners() {
        etOpeningTime.setOnClickListener { showTimePicker(etOpeningTime) }
        etClosingTime.setOnClickListener { showTimePicker(etClosingTime) }

        btnCompleteSetup.setOnClickListener {
            val name = etRestaurantName.text.toString().trim()
            val location = etLocation.text.toString().trim()
            val opening = etOpeningTime.text.toString().trim()
            val closing = etClosingTime.text.toString().trim()

            if (validateInputs(name, location, opening, closing)) {
                viewModel.completeSetup(name, location, opening, closing)
            }
        }
    }

    private fun showTimePicker(target: EditText) {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hour, minute -> target.setText(String.format("%02d:%02d", hour, minute)) },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.setupState.collect { state ->
                    when (state) {
                        is RestaurantSetupState.Idle -> setLoading(false)
                        is RestaurantSetupState.Loading -> setLoading(true)
                        is RestaurantSetupState.Success -> {
                            setLoading(false)
                            sessionManager.setHasRestaurant(true)
                            Toast.makeText(this@RestaurantSetupActivity, "Restaurant profile created!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RestaurantSetupActivity, DashboardActivity::class.java))
                            finish()
                        }
                        is RestaurantSetupState.Error -> {
                            setLoading(false)
                            Toast.makeText(this@RestaurantSetupActivity, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetState()
                        }
                    }
                }
            }
        }
    }

    private fun validateInputs(name: String, location: String, opening: String, closing: String): Boolean {
        if (name.isBlank()) {
            etRestaurantName.error = "Name is required"
            return false
        }
        if (location.isBlank()) {
            etLocation.error = "Location is required"
            return false
        }
        if (opening.isBlank()) {
            etOpeningTime.error = "Opening time is required"
            return false
        }
        if (closing.isBlank()) {
            etClosingTime.error = "Closing time is required"
            return false
        }
        return true
    }

    private fun setLoading(isLoading: Boolean) {
        btnCompleteSetup.isEnabled = !isLoading
    }
}
