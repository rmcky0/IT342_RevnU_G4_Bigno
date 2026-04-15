package com.revnu.mobile.ui.main

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.revnu.mobile.ui.fragments.HomeFragment
import com.revnu.mobile.R
import com.revnu.mobile.ui.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {

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

    private val selectedColor = Color.WHITE
    private val unselectedColor = Color.parseColor("#111243")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        setupBottomNav()

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            selectTab("home")
        }
    }

    private fun bindViews() {
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
//            loadFragment(SalesFragment())
            selectTab("sales")
        }

        navExpenses.setOnClickListener {
//            loadFragment(ExpensesFragment())
            selectTab("expenses")
        }

        navHome.setOnClickListener {
            loadFragment(HomeFragment())
            selectTab("home")
        }

        navStaff.setOnClickListener {
//            loadFragment(StaffFragment())
            selectTab("staff")
        }

        navSettings.setOnClickListener {
            loadFragment(SettingsFragment())
            selectTab("settings")
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun selectTab(selected: String) {
        val tabs = listOf(navSales, navExpenses, navHome, navStaff, navSettings)
        val texts = listOf(textSales, textExpenses, textHome, textStaff, textSettings)
        val icons = listOf(iconSales, iconExpenses, iconHome, iconStaff, iconSettings)

        tabs.forEach {
            it.setBackgroundResource(R.drawable.bg_bottom_nav_unselected)
        }

        texts.forEach {
            it.setTextColor(unselectedColor)
            it.setTypeface(null, Typeface.NORMAL)
        }

        icons.forEach {
            ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(unselectedColor))
        }

        when (selected) {
            "sales" -> {
                navSales.setBackgroundResource(R.drawable.bg_bottom_nav_selected)
                textSales.setTextColor(selectedColor)
                textSales.setTypeface(null, Typeface.BOLD)
                ImageViewCompat.setImageTintList(iconSales, ColorStateList.valueOf(selectedColor))
            }

            "expenses" -> {
                navExpenses.setBackgroundResource(R.drawable.bg_bottom_nav_selected)
                textExpenses.setTextColor(selectedColor)
                textExpenses.setTypeface(null, Typeface.BOLD)
                ImageViewCompat.setImageTintList(iconExpenses, ColorStateList.valueOf(selectedColor))
            }

            "home" -> {
                navHome.setBackgroundResource(R.drawable.bg_bottom_nav_selected)
                textHome.setTextColor(selectedColor)
                textHome.setTypeface(null, Typeface.BOLD)
                ImageViewCompat.setImageTintList(iconHome, ColorStateList.valueOf(selectedColor))
            }

            "staff" -> {
                navStaff.setBackgroundResource(R.drawable.bg_bottom_nav_selected)
                textStaff.setTextColor(selectedColor)
                textStaff.setTypeface(null, Typeface.BOLD)
                ImageViewCompat.setImageTintList(iconStaff, ColorStateList.valueOf(selectedColor))
            }

            "settings" -> {
                navSettings.setBackgroundResource(R.drawable.bg_bottom_nav_selected)
                textSettings.setTextColor(selectedColor)
                textSettings.setTypeface(null, Typeface.BOLD)
                ImageViewCompat.setImageTintList(iconSettings, ColorStateList.valueOf(selectedColor))
            }
        }
    }
}