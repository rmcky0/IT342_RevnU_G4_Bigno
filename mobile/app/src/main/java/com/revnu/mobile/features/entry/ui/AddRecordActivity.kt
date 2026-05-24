package com.revnu.mobile.features.entry.ui

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.revnu.mobile.R
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.features.categories.ui.CategoryPickerBottomSheet
import com.revnu.mobile.features.entry.model.AddRecordState
import com.revnu.mobile.features.entry.viewmodel.AddRecordViewModel
import com.revnu.mobile.features.expenses.repository.ExpensesRepository
import com.revnu.mobile.features.sales.repository.SalesRepository
import kotlinx.coroutines.launch
import java.io.File

class AddRecordActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SAVED_TYPE = "saved_type"
        const val EXTRA_IS_EDIT_MODE = "is_edit_mode"
        const val EXTRA_RECORD_ID = "record_id"
        const val EXTRA_RECORD_TYPE = "record_type"
        const val EXTRA_AMOUNT = "record_amount"
        const val EXTRA_DESCRIPTION = "record_desc"
        const val EXTRA_CATEGORY_ID = "record_category_id"
        const val EXTRA_CATEGORY_NAME = "record_category_name"
        const val EXTRA_START_TAB = "start_tab"
    }

    private lateinit var mainBackground: ConstraintLayout
    private lateinit var tvAmount: TextView
    private lateinit var tvSign: TextView
    private lateinit var layoutKeypad: android.widget.GridLayout
    private lateinit var layoutNotes: LinearLayout
    private lateinit var btnToggleNotes: LinearLayout
    private lateinit var btnClose: ImageButton
    private lateinit var tabLayout: TabLayout
    private lateinit var etNotes: EditText
    private lateinit var tvSelectedCategory: TextView
    private lateinit var btnSave: ImageButton
    private lateinit var btnReceiptPhoto: MaterialCardView
    private lateinit var ivReceiptThumbnail: ImageView
    private lateinit var tvReceiptAction: TextView

    private lateinit var viewModel: AddRecordViewModel

    private var currentAmount = "0"
    private var isNotesVisible = false
    private var isSaleMode = true
    private var selectedCategoryId: String? = null
    private var receiptPhotoUri: Uri? = null

    private var isEditMode = false
    private var editRecordId: String? = null

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            ivReceiptThumbnail.visibility = View.VISIBLE
            ivReceiptThumbnail.setImageURI(receiptPhotoUri)
            tvReceiptAction.text = "TAP TO CHANGE PHOTO"
        }
    }

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) launchCamera()
        else Toast.makeText(this, "Camera permission is required to add a receipt", Toast.LENGTH_SHORT).show()
    }

    private val colorPurpleMain by lazy { Color.parseColor("#8B5CF6") }
    private val colorPurpleDark by lazy { Color.parseColor("#7C3AED") }
    private val colorRedMain by lazy { Color.parseColor("#EF4444") }
    private val colorRedDark by lazy { Color.parseColor("#DC2626") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_record)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val salesRepo = SalesRepository(RetrofitClient.apiService)
                val expRepo = ExpensesRepository(RetrofitClient.apiService)
                @Suppress("UNCHECKED_CAST")
                return AddRecordViewModel(salesRepo, expRepo) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[AddRecordViewModel::class.java]

        bindViews()
        checkEditMode()
        if (!isEditMode && intent.getStringExtra(EXTRA_START_TAB) == "expense") {
            isSaleMode = false
            tabLayout.getTabAt(1)?.select()
        }
        setupKeypad()
        setupListeners()
        observeViewModel()

        updateThemeColors()
    }

    private fun bindViews() {
        mainBackground = findViewById(R.id.mainBackground)
        tvAmount = findViewById(R.id.tvAmount)
        tvSign = findViewById(R.id.tvSign)
        layoutKeypad = findViewById(R.id.layoutKeypad)
        layoutNotes = findViewById(R.id.layoutNotes)
        btnToggleNotes = findViewById(R.id.btnToggleNotes)
        btnClose = findViewById(R.id.btnClose)
        tabLayout = findViewById(R.id.tabLayout)
        etNotes = findViewById(R.id.etNotes)
        tvSelectedCategory = findViewById(R.id.tvSelectedCategory)
        btnSave = findViewById(R.id.btnSave)
        btnReceiptPhoto = findViewById(R.id.btnReceiptPhoto)
        ivReceiptThumbnail = findViewById(R.id.ivReceiptThumbnail)
        tvReceiptAction = findViewById(R.id.tvReceiptAction)
    }

    private fun checkEditMode() {
        isEditMode = intent.getBooleanExtra(EXTRA_IS_EDIT_MODE, false)
        if (isEditMode) {
            editRecordId = intent.getStringExtra(EXTRA_RECORD_ID)
            val type = intent.getStringExtra(EXTRA_RECORD_TYPE)
            isSaleMode = (type == "sale")
            tabLayout.getTabAt(if (isSaleMode) 0 else 1)?.select()

            val tabStrip = tabLayout.getChildAt(0) as LinearLayout
            for (i in 0 until tabStrip.childCount) {
                tabStrip.getChildAt(i).setOnTouchListener { _, _ -> true }
            }

            val rawAmount = intent.getDoubleExtra(EXTRA_AMOUNT, 0.0)
            currentAmount = if (rawAmount % 1.0 == 0.0) rawAmount.toInt().toString() else rawAmount.toString()
            updateAmountDisplay()

            etNotes.setText(intent.getStringExtra(EXTRA_DESCRIPTION))
            selectedCategoryId = intent.getStringExtra(EXTRA_CATEGORY_ID)
            tvSelectedCategory.text = intent.getStringExtra(EXTRA_CATEGORY_NAME) ?: "SELECT CATEGORY"
        }
    }

    private fun updateThemeColors() {
        if (isSaleMode) {
            mainBackground.setBackgroundColor(colorPurpleMain)
            btnToggleNotes.setBackgroundColor(colorPurpleDark)
            tvSign.text = "+"
            btnReceiptPhoto.visibility = View.GONE
        } else {
            mainBackground.setBackgroundColor(colorRedMain)
            btnToggleNotes.setBackgroundColor(colorRedDark)
            tvSign.text = "-"
            btnReceiptPhoto.visibility = View.VISIBLE
        }
    }

    private fun setupListeners() {
        btnClose.setOnClickListener { finish() }

        if (!isEditMode) {
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    isSaleMode = tab?.position == 0
                    updateThemeColors()
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }

        btnToggleNotes.setOnClickListener {
            isNotesVisible = !isNotesVisible

            val iconView = btnToggleNotes.getChildAt(1) as ImageView
            val textView = btnToggleNotes.getChildAt(0) as TextView

            if (isNotesVisible) {
                layoutKeypad.visibility = View.GONE
                layoutNotes.visibility = View.VISIBLE
                iconView.setImageResource(android.R.drawable.arrow_up_float)
                textView.text = "HIDE NOTES"
                etNotes.requestFocus()
                showKeyboard(etNotes)
            } else {
                hideKeyboard()
                etNotes.clearFocus()
                layoutNotes.visibility = View.GONE
                layoutKeypad.visibility = View.VISIBLE
                iconView.setImageResource(android.R.drawable.arrow_down_float)
                textView.text = "NOTES"
            }
        }

        findViewById<MaterialCardView>(R.id.btnCategory).setOnClickListener {
            val type = if (isSaleMode) "SALE" else "EXPENSE"
            val picker = CategoryPickerBottomSheet.newInstance(type)
            picker.onCategorySelected = { category ->
                selectedCategoryId = category.id
                tvSelectedCategory.text = category.name
            }
            picker.show(supportFragmentManager, "category_picker")
        }

        btnReceiptPhoto.setOnClickListener {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        val photoFile = File(cacheDir, "receipts").also { it.mkdirs() }
            .let { File(it, "receipt_${System.currentTimeMillis()}.jpg") }
        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
        receiptPhotoUri = uri
        takePicture.launch(uri)
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    private fun setupKeypad() {
        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn00
        )

        for (id in numberButtons) {
            findViewById<Button>(id).setOnClickListener {
                appendDigit((it as Button).text.toString())
            }
        }

        findViewById<Button>(R.id.btnDot).setOnClickListener {
            if (!currentAmount.contains(".")) appendDigit(".")
        }

        findViewById<ImageButton>(R.id.btnDel).setOnClickListener {
            currentAmount = if (currentAmount.length > 1) currentAmount.dropLast(1) else "0"
            updateAmountDisplay()
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            currentAmount = "0"
            updateAmountDisplay()
        }

        btnSave.setOnClickListener {
            val amount = currentAmount.toDoubleOrNull()
            if (amount == null || amount <= 0.0) {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedCategoryId == null) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.saveRecord(isSaleMode, amount, etNotes.text.toString().trim(), selectedCategoryId!!, editRecordId)
        }
    }

    private fun appendDigit(digit: String) {
        if (currentAmount == "0" && digit != ".") {
            currentAmount = digit
        } else {
            if (currentAmount.contains(".")) {
                val decimalPart = currentAmount.substringAfter(".")
                if (decimalPart.length >= 2) return
            }
            currentAmount += digit
        }
        updateAmountDisplay()
    }

    private fun updateAmountDisplay() {
        tvAmount.text = currentAmount
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.recordState.collect { state ->
                    when (state) {
                        is AddRecordState.Idle -> btnSave.isEnabled = true
                        is AddRecordState.Loading -> btnSave.isEnabled = false
                        is AddRecordState.Success -> {
                            val intent = Intent()
                                .putExtra(EXTRA_SAVED_TYPE, state.savedType)
                                .putExtra("is_edit", isEditMode)
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                        is AddRecordState.Error -> {
                            btnSave.isEnabled = true
                            Toast.makeText(this@AddRecordActivity, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetState()
                        }
                    }
                }
            }
        }
    }
}