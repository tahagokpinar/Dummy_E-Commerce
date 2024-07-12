package com.example.afinal.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.R
import com.example.afinal.models.Cart
import com.example.afinal.models.CartProduct

class CartAdapter(private val carts: List<Cart>) : RecyclerView.Adapter<ProductViewHolder>() {

    private val products: List<CartProduct> = carts.flatMap { it.products }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_row, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val cartProduct = products[position]
        holder.bindCart(cartProduct)
    }
}