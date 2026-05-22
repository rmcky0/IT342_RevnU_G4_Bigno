package com.revnu.mobile.features.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.revnu.mobile.R
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.features.settings.repository.SettingsRepository
import com.revnu.mobile.features.settings.viewmodel.SettingsState
import com.revnu.mobile.features.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(SettingsRepository(RetrofitClient.apiService)) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        val etCurrent = findViewById<TextInputEditText>(R.id.etCurrentPassword)
        val etNew = findViewById<TextInputEditText>(R.id.etNewPassword)
        val etConfirm = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val btnSave = findViewById<MaterialButton>(R.id.btnSavePassword)

        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is SettingsState.Loading -> btnSave.isEnabled = false
                        is SettingsState.Success -> {
                            Toast.makeText(this@ChangePasswordActivity, state.message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        is SettingsState.Error -> {
                            btnSave.isEnabled = true
                            Toast.makeText(this@ChangePasswordActivity, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetState()
                        }
                        else -> btnSave.isEnabled = true
                    }
                }
            }
        }

        btnSave.setOnClickListener {
            val current = etCurrent.text.toString().trim()
            val newPwd = etNew.text.toString().trim()
            val confirm = etConfirm.text.toString().trim()

            if (current.isEmpty()) { etCurrent.error = "Required"; return@setOnClickListener }
            if (newPwd.length < 8) { etNew.error = "Minimum 8 characters"; return@setOnClickListener }
            if (newPwd != confirm) { etConfirm.error = "Passwords do not match"; return@setOnClickListener }

            viewModel.changePassword(current, newPwd)
        }
    }
}
