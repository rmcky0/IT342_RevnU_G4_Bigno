package com.revnu.mobile.features.tenant.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.revnu.mobile.features.analytics.ui.AnalyticsFragment
import com.revnu.mobile.R
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.core.session.SessionManager
import com.revnu.mobile.features.auth.ui.LoginActivity
import com.revnu.mobile.features.expenses.ui.ExpensesFragment
import com.revnu.mobile.features.sales.ui.SalesFragment
import com.revnu.mobile.features.settings.SettingsFragment
import com.revnu.mobile.features.staff.ui.StaffFragment
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var navSales: LinearLayout
    private lateinit var navExpenses: LinearLayout
    private lateinit var navHome: LinearLayout
    private lateinit var navStaff: LinearLayout
    private lateinit var navSettings: LinearLayout

    private lateinit var iconSales: ImageView
    private lateinit var iconExpenses: ImageView
    private lateinit var iconHome: ImageView
    private lateinit var iconStaff: ImageView
    private lateinit var iconSettings: ImageView

    private lateinit var textSales: TextView
    private lateinit var textExpenses: TextView
    private lateinit var textHome: TextView
    private lateinit var textStaff: TextView
    private lateinit var textSettings: TextView
    private lateinit var sessionManager: SessionManager
    private lateinit var fabNewRecord: ImageButton
    private var currentTab = "home"

    private val selectedColor = Color.WHITE
    private val unselectedColor = Color.parseColor("#111243")

    private val newRecordLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val savedType = result.data?.getStringExtra(NewRecordActivity.EXTRA_SAVED_TYPE)
            if (savedType == "sales") {
                loadFragment(SalesFragment())
                selectTab("sales")
            } else if (savedType == "expense") {
                loadFragment(ExpensesFragment())
                selectTab("expenses")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_portal)

        sessionManager = SessionManager(this)

        bindViews()
        setupBottomNav()
        setupFab()

        if (savedInstanceState == null) {
            loadFragment(AnalyticsFragment())
            selectTab("home")
        }

        // Check token validity on startup
        fetchUserProfile()
    }

    private fun bindViews() {
        fabNewRecord = findViewById(R.id.fabNewRecord)
        navSales = findViewById(R.id.navSales)
        navExpenses = findViewById(R.id.navExpenses)
        navHome = findViewById(R.id.navHome)
        navStaff = findViewById(R.id.navStaff)
        navSettings = findViewById(R.id.navSettings)

        iconSales = findViewById(R.id.iconSales)
        iconExpenses = findViewById(R.id.iconExpenses)
        iconHome = findViewById(R.id.iconHome)
        iconStaff = findViewById(R.id.iconStaff)
        iconSettings = findViewById(R.id.iconSettings)

        textSales = findViewById(R.id.textSales)
        textExpenses = findViewById(R.id.textExpenses)
        textHome = findViewById(R.id.textHome)
        textStaff = findViewById(R.id.textStaff)
        textSettings = findViewById(R.id.textSettings)
    }

    private fun setupBottomNav() {
        navSales.setOnClickListener {
            loadFragment(SalesFragment())
            selectTab("sales")
        }
        navExpenses.setOnClickListener {
            loadFragment(ExpensesFragment())
            selectTab("expenses")
        }
        navHome.setOnClickListener {
            loadFragment(AnalyticsFragment())
            selectTab("home")
        }
        navStaff.setOnClickListener {
            loadFragment(StaffFragment())
            selectTab("staff")
        }
        navSettings.setOnClickListener {
            loadFragment(SettingsFragment())
            selectTab("settings")
        }
    }

    private fun setupFab() {
        fabNewRecord.setOnClickListener {
            newRecordLauncher.launch(Intent(this, NewRecordActivity::class.java))
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun selectTab(selected: String) {
        // Reset all tabs to unselected first
        setTabStyle(navSales, textSales, iconSales, false)
        setTabStyle(navExpenses, textExpenses, iconExpenses, false)
        setTabStyle(navHome, textHome, iconHome, false)
        setTabStyle(navStaff, textStaff, iconStaff, false)
        setTabStyle(navSettings, textSettings, iconSettings, false)

        // Highlight the selected one
        when (selected) {
            "sales" -> setTabStyle(navSales, textSales, iconSales, true)
            "expenses" -> setTabStyle(navExpenses, textExpenses, iconExpenses, true)
            "home" -> setTabStyle(navHome, textHome, iconHome, true)
            "staff" -> setTabStyle(navStaff, textStaff, iconStaff, true)
            "settings" -> setTabStyle(navSettings, textSettings, iconSettings, true)
        }
    }

    private fun setTabStyle(layout: LinearLayout, text: TextView, icon: ImageView, isSelected: Boolean) {
        if (isSelected) {
            layout.setBackgroundResource(R.drawable.bg_bottom_nav_selected)
            text.setTextColor(selectedColor)
            text.setTypeface(null, Typeface.BOLD)
            ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(selectedColor))
        } else {
            layout.setBackgroundResource(R.drawable.bg_bottom_nav_unselected)
            text.setTextColor(unselectedColor)
            text.setTypeface(null, Typeface.NORMAL)
            ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(unselectedColor))
        }
    }

    private fun fetchUserProfile() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getMe()
                if (!response.isSuccessful) {
                    if (response.code() == 401 || response.code() == 403) {
                        Toast.makeText(this@HomeActivity, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
                        sessionManager.clearSession()
                        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                        finish()
                    }
                }
            } catch (e: Exception) {
                Log.e("TENANT_PORTAL", "Network error on /me", e)
            }
        }
    }
}

