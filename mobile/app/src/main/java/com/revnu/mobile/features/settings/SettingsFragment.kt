package com.revnu.mobile.features.settings

import android.Manifest
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.revnu.mobile.R
import com.revnu.mobile.BuildConfig
import com.revnu.mobile.core.config.Constants
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.core.session.SessionManager
import com.revnu.mobile.features.auth.ui.WelcomeActivity
import com.revnu.mobile.features.settings.model.RestaurantProfileRequest
import com.revnu.mobile.features.settings.model.RestaurantProfileResponse
import com.revnu.mobile.features.settings.repository.SettingsRepository
import com.revnu.mobile.features.settings.viewmodel.SettingsState
import com.revnu.mobile.features.settings.viewmodel.SettingsViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var sessionManager: SessionManager

    private lateinit var ivRestaurantLogo: CircleImageView
    private lateinit var tvLogoRestaurantName: TextView
    private lateinit var tvRestaurantName: TextView
    private lateinit var tvRestaurantAddress: TextView
    private lateinit var tvOpeningHours: TextView
    private lateinit var tvClosingHours: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserFullName: TextView

    private var currentRestaurantProfile: RestaurantProfileResponse? = null
    private var cameraLogoFile: File? = null

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) launchCamera()
        else Toast.makeText(requireContext(), "Camera permission is required to take a photo", Toast.LENGTH_SHORT).show()
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraLogoFile?.let { file ->
                val body = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", file.name, body)
                viewModel.uploadLogo(part)
            }
        }
    }

    private val logoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { handleLogoSelected(it) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(SettingsRepository(RetrofitClient.apiService)) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        bindViews(view)
        setupClickListeners(view)
        observeViewModel()

        viewModel.loadProfile()
        viewModel.loadRestaurantProfile()
    }

    private fun bindViews(view: View) {
        ivRestaurantLogo = view.findViewById(R.id.ivRestaurantLogo)
        tvLogoRestaurantName = view.findViewById(R.id.tvLogoRestaurantName)
        tvRestaurantName = view.findViewById(R.id.tvRestaurantName)
        tvRestaurantAddress = view.findViewById(R.id.tvRestaurantAddress)
        tvOpeningHours = view.findViewById(R.id.tvOpeningHours)
        tvClosingHours = view.findViewById(R.id.tvClosingHours)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        tvUserFullName = view.findViewById(R.id.tvUserFullName)
    }

    private fun setupClickListeners(view: View) {
        val logoTap = View.OnClickListener { showLogoSourceDialog() }
        ivRestaurantLogo.setOnClickListener(logoTap)
        view.findViewById<View>(R.id.btnChangeLogo).setOnClickListener(logoTap)

        view.findViewById<View>(R.id.rowRestaurantName).setOnClickListener {
            val p = currentRestaurantProfile ?: return@setOnClickListener
            showEditDialog("Restaurant Name", p.restaurantName) { v ->
                viewModel.saveRestaurantProfile(RestaurantProfileRequest(v, p.physicalAddress, p.openingHours, p.closingHours))
            }
        }

        view.findViewById<View>(R.id.rowRestaurantAddress).setOnClickListener {
            val p = currentRestaurantProfile ?: return@setOnClickListener
            showEditDialog("Address", p.physicalAddress) { v ->
                viewModel.saveRestaurantProfile(RestaurantProfileRequest(p.restaurantName, v, p.openingHours, p.closingHours))
            }
        }

        view.findViewById<View>(R.id.rowOpeningHours).setOnClickListener {
            val p = currentRestaurantProfile ?: return@setOnClickListener
            showTimePicker("Opening Time", p.openingHours) { v ->
                viewModel.saveRestaurantProfile(RestaurantProfileRequest(p.restaurantName, p.physicalAddress, v, p.closingHours))
            }
        }

        view.findViewById<View>(R.id.rowClosingHours).setOnClickListener {
            val p = currentRestaurantProfile ?: return@setOnClickListener
            showTimePicker("Closing Time", p.closingHours) { v ->
                viewModel.saveRestaurantProfile(RestaurantProfileRequest(p.restaurantName, p.physicalAddress, p.openingHours, v))
            }
        }

        view.findViewById<View>(R.id.rowFullName).setOnClickListener {
            val current = tvUserFullName.text.toString().takeIf { it != "—" } ?: ""
            showEditDialog("Full Name", current) { v -> viewModel.saveProfile(v) }
        }

        view.findViewById<View>(R.id.rowChangePassword).setOnClickListener {
            startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
        }

        view.findViewById<View>(R.id.rowLogout).setOnClickListener { confirmLogout() }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userProfile.collect { profile ->
                        profile ?: return@collect
                        tvUserEmail.text = profile.email
                        tvUserFullName.text = profile.fullname
                    }
                }

                launch {
                    viewModel.restaurantProfile.collect { profile ->
                        profile ?: return@collect
                        currentRestaurantProfile = profile
                        tvLogoRestaurantName.text = profile.restaurantName
                        tvRestaurantName.text = profile.restaurantName
                        tvRestaurantAddress.text = profile.physicalAddress
                        tvOpeningHours.text = profile.openingHours
                        tvClosingHours.text = profile.closingHours
                        profile.logoFileId?.let { loadLogoFromId(it) }
                    }
                }

                launch {
                    viewModel.state.collect { state ->
                        when (state) {
                            is SettingsState.Success -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                                viewModel.resetState()
                            }
                            is SettingsState.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                                viewModel.resetState()
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun showEditDialog(title: String, currentValue: String, onSave: (String) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_field, null)
        val til = dialogView.findViewById<TextInputLayout>(R.id.tilDialogField)
        val et = dialogView.findViewById<TextInputEditText>(R.id.etDialogField)
        til.hint = title
        et.setText(currentValue)
        et.setSelection(currentValue.length)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                val value = et.text.toString().trim()
                if (value.isNotEmpty()) onSave(value)
                else Toast.makeText(requireContext(), "Field cannot be empty", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showTimePicker(title: String, currentTime: String, onSave: (String) -> Unit) {
        val parts = currentTime.split(":").mapNotNull { it.toIntOrNull() }
        val hour = parts.getOrElse(0) { 8 }
        val minute = parts.getOrElse(1) { 0 }
        TimePickerDialog(requireContext(), { _, h, m ->
            onSave(String.format("%02d:%02d", h, m))
        }, hour, minute, false).apply { setTitle(title) }.show()
    }

    private fun loadLogoFromId(fileId: String) {
        val baseUrl = if (BuildConfig.DEBUG) Constants.BASE_URL_DEBUG else Constants.BASE_URL_RELEASE
        val url = "${baseUrl}files/$fileId"
        val token = sessionManager.fetchAccessToken() ?: return
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                val bytes = client.newCall(request).execute().body?.bytes() ?: return@launch
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                withContext(Dispatchers.Main) { ivRestaurantLogo.setImageBitmap(bitmap) }
            } catch (_: Exception) {}
        }
    }

    private fun handleLogoSelected(uri: Uri) {
        try {
            val input = requireContext().contentResolver.openInputStream(uri) ?: return
            val tmp = File(requireContext().cacheDir, "logo_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tmp).use { input.copyTo(it) }
            val body = tmp.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", tmp.name, body)
            viewModel.uploadLogo(part)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to process image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Change Logo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            launchCamera()
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                    1 -> logoPickerLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun launchCamera() {
        val logosDir = File(requireContext().cacheDir, "logos").also { it.mkdirs() }
        val file = File(logosDir, "logo_${System.currentTimeMillis()}.jpg")
        cameraLogoFile = file
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )
        cameraLauncher.launch(uri)
    }

    private fun confirmLogout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Log Out") { _, _ ->
                sessionManager.clearSession()
                startActivity(
                    Intent(requireContext(), WelcomeActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
                requireActivity().finish()
            }
            .show()
    }
}
