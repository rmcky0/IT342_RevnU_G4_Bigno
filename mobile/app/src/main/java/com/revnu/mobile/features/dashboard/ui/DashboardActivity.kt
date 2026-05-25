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

    private val selectedColor = Color.parseColor("#6366F1")
    private val unselectedColor = Color.parseColor("#94A3B8")

    private lateinit var analyticsFragment: AnalyticsFragment
    private lateinit var salesFragment: SalesFragment
    private lateinit var expensesFragment: ExpensesFragment
    private lateinit var staffFragment: StaffFragment
    private lateinit var settingsFragment: SettingsFragment

    private lateinit var activeFragment: Fragment

    private val newRecordLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val savedType = result.data?.getStringExtra(AddRecordActivity.EXTRA_SAVED_TYPE)
            val isEdit = result.data?.getBooleanExtra("is_edit", false) ?: false
            Toast.makeText(this, if (isEdit) "Record updated" else "Record saved", Toast.LENGTH_SHORT).show()
            when (savedType) {
                "sale" -> { showFragment(salesFragment); selectTab("sales"); salesFragment.refresh() }
                "expense" -> { showFragment(expensesFragment); selectTab("expenses"); expensesFragment.refresh() }
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
        resolveFragments(savedInstanceState)
    }

    private fun resolveFragments(savedInstanceState: Bundle?) {
        val fm = supportFragmentManager

        analyticsFragment = fm.findFragmentByTag(TAB_HOME) as? AnalyticsFragment ?: AnalyticsFragment()
        salesFragment     = fm.findFragmentByTag(TAB_SALES) as? SalesFragment ?: SalesFragment()
        expensesFragment  = fm.findFragmentByTag(TAB_EXPENSES) as? ExpensesFragment ?: ExpensesFragment()
        staffFragment     = fm.findFragmentByTag(TAB_STAFF) as? StaffFragment ?: StaffFragment()
        settingsFragment  = fm.findFragmentByTag(TAB_SETTINGS) as? SettingsFragment ?: SettingsFragment()

        if (savedInstanceState == null) {
            fm.beginTransaction()
                .add(R.id.fragmentContainer, settingsFragment, TAB_SETTINGS).hide(settingsFragment)
                .add(R.id.fragmentContainer, staffFragment, TAB_STAFF).hide(staffFragment)
                .add(R.id.fragmentContainer, expensesFragment, TAB_EXPENSES).hide(expensesFragment)
                .add(R.id.fragmentContainer, salesFragment, TAB_SALES).hide(salesFragment)
                .add(R.id.fragmentContainer, analyticsFragment, TAB_HOME)
                .commitNow()
            activeFragment = analyticsFragment
            selectTab(TAB_HOME)
        } else {
            val restoredTab = savedInstanceState.getString(KEY_ACTIVE_TAB, TAB_HOME) ?: TAB_HOME
            currentTab = restoredTab
            activeFragment = fragmentForTab(restoredTab)
            selectTab(restoredTab)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_ACTIVE_TAB, currentTab)
    }

    private fun bindViews() {
        fabNewRecord  = findViewById(R.id.fabNewRecord)
        navSales      = findViewById(R.id.navSales)
        navExpenses   = findViewById(R.id.navExpenses)
        navHome       = findViewById(R.id.navHome)
        navStaff      = findViewById(R.id.navStaff)
        navSettings   = findViewById(R.id.navSettings)
        iconSales     = findViewById(R.id.iconSales)
        iconExpenses  = findViewById(R.id.iconExpenses)
        iconHome      = findViewById(R.id.iconHome)
        iconStaff     = findViewById(R.id.iconStaff)
        iconSettings  = findViewById(R.id.iconSettings)
        textSales     = findViewById(R.id.textSales)
        textExpenses  = findViewById(R.id.textExpenses)
        textHome      = findViewById(R.id.textHome)
        textStaff     = findViewById(R.id.textStaff)
        textSettings  = findViewById(R.id.textSettings)
    }

    private fun setupBottomNav() {
        navSales.setOnClickListener    { showFragment(salesFragment);     selectTab(TAB_SALES) }
        navExpenses.setOnClickListener { showFragment(expensesFragment);  selectTab(TAB_EXPENSES) }
        navHome.setOnClickListener     { showFragment(analyticsFragment); selectTab(TAB_HOME) }
        navStaff.setOnClickListener    { showFragment(staffFragment);     selectTab(TAB_STAFF) }
        navSettings.setOnClickListener { showFragment(settingsFragment);  selectTab(TAB_SETTINGS) }
    }

    private fun setupFab() {
        fabNewRecord.setOnClickListener {
            val intent = Intent(this, AddRecordActivity::class.java).apply {
                if (currentTab == TAB_EXPENSES) putExtra(AddRecordActivity.EXTRA_START_TAB, "expense")
            }
            newRecordLauncher.launch(intent)
        }
    }

    private fun showFragment(target: Fragment) {
        if (!target.isHidden) return
        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(target)
            .commitNow()
        activeFragment = target
    }

    private fun selectTab(selected: String) {
        currentTab = selected
        listOf(textSales to iconSales, textExpenses to iconExpenses, textHome to iconHome,
            textStaff to iconStaff, textSettings to iconSettings)
            .forEach { (tv, iv) -> setTabStyle(tv, iv, false) }

        when (selected) {
            TAB_SALES     -> { setTabStyle(textSales, iconSales, true);       fabVisibility(false) }
            TAB_EXPENSES  -> { setTabStyle(textExpenses, iconExpenses, true);  fabVisibility(false) }
            TAB_HOME      -> { setTabStyle(textHome, iconHome, true);          fabVisibility(false) }
            TAB_STAFF     -> { setTabStyle(textStaff, iconStaff, true);        fabVisibility(true) }
            TAB_SETTINGS  -> { setTabStyle(textSettings, iconSettings, true);  fabVisibility(true) }
        }
    }

    private fun fabVisibility(isHidden: Boolean) {
        fabNewRecord.visibility = if (isHidden) View.GONE else View.VISIBLE
    }

    private fun setTabStyle(text: TextView, icon: ImageView, isSelected: Boolean) {
        val color = if (isSelected) selectedColor else unselectedColor
        text.setTextColor(color)
        ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(color))
        text.visibility = if (isSelected) View.VISIBLE else View.GONE
    }

    private fun fragmentForTab(tab: String): Fragment = when (tab) {
        TAB_SALES    -> salesFragment
        TAB_EXPENSES -> expensesFragment
        TAB_STAFF    -> staffFragment
        TAB_SETTINGS -> settingsFragment
        else         -> analyticsFragment
    }

    companion object {
        private const val TAB_HOME     = "home"
        private const val TAB_SALES    = "sales"
        private const val TAB_EXPENSES = "expenses"
        private const val TAB_STAFF    = "staff"
        private const val TAB_SETTINGS = "settings"
        private const val KEY_ACTIVE_TAB = "active_tab"
    }
}
