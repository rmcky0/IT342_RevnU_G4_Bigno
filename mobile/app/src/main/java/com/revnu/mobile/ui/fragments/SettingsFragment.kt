package com.revnu.mobile.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.revnu.mobile.R
import com.revnu.mobile.auth.LoginActivity
import com.revnu.mobile.utils.SessionManager
import com.revnu.mobile.utils.TokenManager

class SettingsFragment : Fragment() {

    private lateinit var cardPersonalProfile: LinearLayout
    private lateinit var cardRestaurantProfile: LinearLayout
    private lateinit var cardLogout: LinearLayout

    private lateinit var tokenManager: TokenManager
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tokenManager = TokenManager(requireContext())
        sessionManager = SessionManager(requireContext())

        cardPersonalProfile = view.findViewById(R.id.cardPersonalProfile)
        cardRestaurantProfile = view.findViewById(R.id.cardRestaurantProfile)
        cardLogout = view.findViewById(R.id.cardLogout)

        setupRoleAccess()
        setupListeners()
    }

    private fun setupRoleAccess() {
        val role = sessionManager.getUserRole()?.uppercase()

        if (role != "OWNER") {
            cardRestaurantProfile.visibility = View.GONE
        } else {
            cardRestaurantProfile.visibility = View.VISIBLE
        }
    }

    private fun setupListeners() {
        cardPersonalProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Personal Profile clicked", Toast.LENGTH_SHORT).show()
        }

        cardRestaurantProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Restaurant Profile clicked", Toast.LENGTH_SHORT).show()
        }

        cardLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        tokenManager.clearToken()
        sessionManager.clearSession()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}