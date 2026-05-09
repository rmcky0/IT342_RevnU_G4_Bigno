package com.revnu.mobile.features.settings.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.revnu.mobile.R

class PersonalProfileFragment : Fragment(R.layout.fragment_personal_profile) {

    private var isEditMode = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnEditToggle = view.findViewById<MaterialButton>(R.id.btnEditToggle)
        val etFullName = view.findViewById<TextInputEditText>(R.id.etFullName)
        val vAvatarOverlay = view.findViewById<View>(R.id.vAvatarOverlay)
        val ivCameraIcon = view.findViewById<ImageView>(R.id.ivCameraIcon)
        val btnChangePassword = view.findViewById<MaterialButton>(R.id.btnChangePassword)

        btnEditToggle.setOnClickListener {
            toggleEditMode(!isEditMode, btnEditToggle, etFullName, vAvatarOverlay, ivCameraIcon)
        }

        btnChangePassword.setOnClickListener {
            // Open Change Password Dialog or Fragment
            Toast.makeText(requireContext(), "Opening password settings...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleEditMode(
        enable: Boolean,
        btn: MaterialButton,
        input: TextInputEditText,
        overlay: View,
        camera: ImageView
    ) {
        isEditMode = enable

        // Update UI
        input.isEnabled = enable
        btn.text = if (enable) "Save" else "Edit"
        btn.setIconResource(if (enable) R.drawable.ic_menu_save else R.drawable.ic_edit)

        overlay.visibility = if (enable) View.VISIBLE else View.GONE
        camera.visibility = if (enable) View.VISIBLE else View.GONE

        if (!enable) {
            // Logic to call ViewModel and save the new Full Name
            Toast.makeText(requireContext(), "Profile Updated", Toast.LENGTH_SHORT).show()
        }
    }
}