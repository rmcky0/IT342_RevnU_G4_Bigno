package com.revnu.mobile.features.categories.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.revnu.mobile.R
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.features.categories.model.CategoryResponse
import com.revnu.mobile.features.categories.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryPickerBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_TYPE = "category_type"

        fun newInstance(type: String) = CategoryPickerBottomSheet().apply {
            arguments = Bundle().apply { putString(ARG_TYPE, type) }
        }
    }

    var onCategorySelected: ((CategoryResponse) -> Unit)? = null

    // Theme Colors
    private val colorPurpleMain by lazy { Color.parseColor("#8B5CF6") }
    private val colorRedMain by lazy { Color.parseColor("#EF4444") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_category_picker, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = arguments?.getString(ARG_TYPE) ?: "SALE"

        val tvTitle = view.findViewById<TextView>(R.id.tvPickerTitle)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)
        val numberPicker = view.findViewById<NumberPicker>(R.id.numberPicker)
        val btnConfirm = view.findViewById<MaterialButton>(R.id.btnConfirm)

        // Set Title text dynamically
        tvTitle.text = "Select ${type.lowercase().replaceFirstChar { it.uppercase() }} Category"

        // Apply Dynamic Theme Color (Purple for Sale, Red for Expense)
        val themeColor = if (type.uppercase() == "SALE") colorPurpleMain else colorRedMain
        val colorStateList = ColorStateList.valueOf(themeColor)

        btnConfirm.backgroundTintList = colorStateList
        progressBar.indeterminateTintList = colorStateList

        progressBar.visibility = View.VISIBLE

        val repository = CategoryRepository(RetrofitClient.apiService)
        lifecycleScope.launch {
            val result = repository.getCategories(type)
            progressBar.visibility = View.GONE

            result.fold(
                onSuccess = { list ->
                    if (list.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                    } else {
                        val names = list.map { it.name }.toTypedArray()
                        numberPicker.minValue = 0
                        numberPicker.maxValue = names.size - 1
                        numberPicker.displayedValues = names
                        numberPicker.wrapSelectorWheel = false

                        numberPicker.visibility = View.VISIBLE
                        btnConfirm.visibility = View.VISIBLE

                        btnConfirm.setOnClickListener {
                            onCategorySelected?.invoke(list[numberPicker.value])
                            dismiss()
                        }
                    }
                },
                onFailure = {
                    tvEmpty.text = "Failed to load categories."
                    tvEmpty.visibility = View.VISIBLE
                }
            )
        }
    }
}