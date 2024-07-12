package com.example.afinal.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.R
import com.example.afinal.models.Categories

class CategoryAdapter(val categoryList: List<Categories>, private val listener: (Categories) -> Unit) :
    RecyclerView.Adapter<ProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_row, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bindCategory(categoryList[position])
        holder.itemView.setOnClickListener {
            listener(categoryList[position])
        }
    }
}