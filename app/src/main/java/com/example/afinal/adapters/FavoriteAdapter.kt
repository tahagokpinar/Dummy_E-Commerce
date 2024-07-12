package com.example.afinal.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.R
import com.example.afinal.entities.Favorite

class FavoriteAdapter(val favoriteList: List<Favorite>, val listener: (Favorite) -> Unit) :
    RecyclerView.Adapter<ProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_row, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return favoriteList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bindFavorite(favoriteList[position])

        holder.itemView.setOnClickListener {
            listener(favoriteList[position])
        }
    }
}