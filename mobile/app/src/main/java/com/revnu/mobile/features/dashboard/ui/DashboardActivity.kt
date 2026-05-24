package com.revnu.mobile.features.dashboard.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.revnu.mobile.R
import com.revnu.mobile.core.session.SessionManager
import com.revnu.mobile.features.analytics.ui.AnalyticsFragment
import com.revnu.mobile.features.entry.ui.AddRecordActivity
import com.revnu.mobile.features.expenses.ui.ExpensesFragment
import com.revnu.mobile.features.sales.ui.SalesFragment
import com.revnu.mobile.features.settings.SettingsFragment
import com.revnu.mobile.features.staff.ui.StaffFragment

class DashboardActivity : AppCompatActivity() {

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

    // Modern Colors matching your new UI
    private val selectedColor = Color.parseColor("#6366F1") // Indigo
    private val unselectedColor = Color.parseColor("#94A3B8") // Slate Gray

    private val newRecordLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val savedType = result.data?.getStringExtra(AddRecordActivity.EXTRA_SAVED_TYPE)
            val isEdit = result.data?.getBooleanExtra("is_edit", false) ?: false
            val msg = if (isEdit) "Record updated" else "Record saved"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            if (savedType == "sale") {
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
        setContentView(R.layout.activity_dashboard)

        sessionManager = SessionManager(this)

        bindViews()
        setupBottomNav()
        setupFab()

        if (savedInstanceState == null) {
            loadFragment(AnalyticsFragment())
            selectTab("home")
        }
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
            val intent = Intent(this, AddRecordActivity::class.java).apply {
                if (currentTab == "expenses") putExtra(AddRecordActivity.EXTRA_START_TAB, "expense")
            }
            newRecordLauncher.launch(intent)
        }
    }


    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun selectTab(selected: String) {
        currentTab = selected
        setTabStyle(textSales, iconSales, false)
        setTabStyle(textExpenses, iconExpenses, false)
        setTabStyle(textHome, iconHome, false)
        setTabStyle(textStaff, iconStaff, false)
        setTabStyle(textSettings, iconSettings, false)

        when (selected) {
            "sales" -> {
                setTabStyle(textSales, iconSales, true)
                fabVisibility(false)
            }
            "expenses" -> {
                setTabStyle(textExpenses, iconExpenses, true)
                fabVisibility(false)}
            "home" -> {
                setTabStyle(textHome, iconHome, true)
                fabVisibility(false)
            }
            "staff" -> {
                setTabStyle(textStaff, iconStaff, true)
                fabVisibility(true)
            }
            "settings" -> {
                setTabStyle(textSettings, iconSettings, true)
                fabVisibility(true)
            }
        }

    }

    private fun fabVisibility(isHidden: Boolean){
        if (isHidden) {
            fabNewRecord.visibility = View.GONE
        }else {
            fabNewRecord.visibility = View.VISIBLE
        }
    }
    private fun setTabStyle(text: TextView, icon: ImageView, isSelected: Boolean) {
        if (isSelected) {
            text.setTextColor(selectedColor)
            ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(selectedColor))
            text.visibility = View.VISIBLE
        } else {
            text.setTextColor(unselectedColor)
            ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(unselectedColor))
            text.visibility = View.GONE
        }
    }
}