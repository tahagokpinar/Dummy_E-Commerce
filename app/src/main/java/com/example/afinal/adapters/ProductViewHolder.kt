package com.example.afinal.adapters

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.afinal.R
import com.example.afinal.configs.ApiClient
import com.example.afinal.entities.Favorite
import com.example.afinal.models.AddCartRequest
import com.example.afinal.models.Cart
import com.example.afinal.models.CartItem
import com.example.afinal.models.CartProduct
import com.example.afinal.models.Categories
import com.example.afinal.models.Product
import com.example.afinal.services.IDummyService
import com.example.afinal.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    lateinit var iDummyService: IDummyService
    lateinit var sharedPreferences: SharedPref

    fun bindProduct(product: Product) {

        iDummyService = ApiClient.getClient().create(IDummyService::class.java)
        sharedPreferences = SharedPref(itemView.context)

        val productName = itemView.findViewById<TextView>(R.id.product_name)
        val productImage = itemView.findViewById<ImageView>(R.id.product_image)
        val productPrice = itemView.findViewById<TextView>(R.id.product_price)
        val productRating = itemView.findViewById<TextView>(R.id.product_rating)
        val addToCartButton = itemView.findViewById<TextView>(R.id.add_to_cart_button)

        productName.text = product.title
        productPrice.text = product.price.toString() + "$"
        productRating.text = product.rating.toString() + "/5"

        val imageUrl = product.images.firstOrNull()
        if (imageUrl != null) {
            Glide.with(itemView.context)
                .load(imageUrl)
                .into(productImage)
        }

        val user = sharedPreferences.getUser()
        val userId = user?.id
        val addCartRequest = AddCartRequest(userId!!, listOf(CartItem(product.id, 1)))
        addToCartButton.setOnClickListener {
            iDummyService.addToCart(addCartRequest).enqueue(object : Callback<Cart> {
                override fun onResponse(p0: Call<Cart>, p1: Response<Cart>) {
                    if (p1.isSuccessful) {
                        Toast.makeText(
                            itemView.context,
                            "Thank you for your order",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("Order", "Cart: ${p1.code()} + ${p1.body()}")
                    }
                }

                override fun onFailure(p0: Call<Cart>, p1: Throwable) {
                    Log.e("Cart", "Failed to add to cart", p1)
                }
            })
        }
    }

    fun bindCategory(category: Categories) {
        val categoryName = itemView.findViewById<TextView>(R.id.category_name)
        categoryName.text = category.name
    }

    fun bindCart(cartProduct: CartProduct) {
        val productName = itemView.findViewById<TextView>(R.id.productNameTextView)
        val productPrice = itemView.findViewById<TextView>(R.id.productPriceTextView)
        val productImage = itemView.findViewById<ImageView>(R.id.productImageView)

        productName.text = cartProduct.title
        productPrice.text = cartProduct.price.toString() + "$"

        val imageUrl = cartProduct.thumbnail
        if (imageUrl != null) {
            Glide.with(itemView.context)
                .load(imageUrl)
                .into(productImage)
        }
    }

    fun bindFavorite(favorite: Favorite) {

        iDummyService = ApiClient.getClient().create(IDummyService::class.java)
        sharedPreferences = SharedPref(itemView.context)

        val productName = itemView.findViewById<TextView>(R.id.product_name)
        val productImage = itemView.findViewById<ImageView>(R.id.product_image)
        val productPrice = itemView.findViewById<TextView>(R.id.product_price)
        val productRating = itemView.findViewById<TextView>(R.id.product_rating)
        val addToCartButton = itemView.findViewById<TextView>(R.id.add_to_cart_button)

        productName.text = favorite.p_title
        productPrice.text = favorite.p_price.toString() + "$"
        productRating.text = favorite.p_rating.toString() + "/5"

        val imageUrl = favorite.p_images.firstOrNull()
        if (imageUrl != null) {
            Glide.with(itemView.context)
                .load(imageUrl)
                .into(productImage)
        }

        val user = sharedPreferences.getUser()
        val userId = user?.id
        val addCartRequest = AddCartRequest(userId!!, listOf(CartItem(favorite.p_id, 1)))
        addToCartButton.setOnClickListener {
            iDummyService.addToCart(addCartRequest).enqueue(object : Callback<Cart> {
                override fun onResponse(p0: Call<Cart>, p1: Response<Cart>) {
                    if (p1.isSuccessful) {
                        Toast.makeText(
                            itemView.context,
                            "Thank you for your order",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("Order", "Cart: ${p1.code()} + ${p1.body()}")
                    }
                }

                override fun onFailure(p0: Call<Cart>, p1: Throwable) {
                    Log.e("Cart", "Failed to add to cart", p1)
                }
            })
        }
    }
}