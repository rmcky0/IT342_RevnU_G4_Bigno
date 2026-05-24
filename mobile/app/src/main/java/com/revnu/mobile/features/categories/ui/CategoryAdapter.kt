package com.revnu.mobile.features.categories.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.revnu.mobile.R
import com.revnu.mobile.features.categories.model.CategoryResponse

class CategoryAdapter(
    private val onSelected: (CategoryResponse) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private val items = mutableListOf<CategoryResponse>()

    fun submitList(list: List<CategoryResponse>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvCategoryName)
        private val tvDefault: TextView = itemView.findViewById(R.id.tvCategoryDefault)

        fun bind(category: CategoryResponse) {
            tvName.text = category.name
            tvDefault.visibility = if (category.isDefault) View.VISIBLE else View.GONE
            itemView.setOnClickListener { onSelected(category) }
        }
    }
}
