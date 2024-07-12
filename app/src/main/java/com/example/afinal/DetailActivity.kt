package com.example.afinal

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.afinal.adapters.ImageAdapter
import com.example.afinal.models.Product
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import com.example.afinal.configs.ApiClient
import com.example.afinal.configs.AppDatabase
import com.example.afinal.entities.Favorite
import com.example.afinal.models.AddCartRequest
import com.example.afinal.models.Cart
import com.example.afinal.models.CartItem
import com.example.afinal.services.IDummyService
import com.example.afinal.utils.SharedPref
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager2
    lateinit var likeButton: FloatingActionButton
    lateinit var addToCartButton: Button
    lateinit var productRating: RatingBar
    lateinit var productTitle: TextView
    lateinit var productDescription: TextView
    lateinit var productDiscountPercentage: TextView
    lateinit var productStock: TextView
    lateinit var productWarrantyInformation: TextView
    lateinit var productShippingInformation: TextView
    lateinit var productMinimumOrderQuantity: TextView
    lateinit var tabLayout: TabLayout
    lateinit var price : TextView

    lateinit var iDummyService: IDummyService
    lateinit var sharedPreferences: SharedPref


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sharedPreferences = SharedPref(this)

        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "favorite_database"
        )
            .allowMainThreadQueries()
            .build()

        val favoriteDao = db.favoriteDao()

        setContentView(R.layout.activity_detail)

        viewPager = findViewById(R.id.productImagesViewPager)
        likeButton = findViewById(R.id.likeButton)
        addToCartButton = findViewById(R.id.addToCartButton)
        productRating = findViewById(R.id.productRating)
        productTitle = findViewById(R.id.productTitle)
        productDescription = findViewById(R.id.productDescription)
        productDiscountPercentage = findViewById(R.id.productDiscountPercentage)
        productStock = findViewById(R.id.productStock)
        productWarrantyInformation = findViewById(R.id.productWarrantyInformation)
        productShippingInformation = findViewById(R.id.productShippingInformation)
        productMinimumOrderQuantity = findViewById(R.id.productMinimumOrderQuantity)
        tabLayout = findViewById(R.id.tabLayout)
        price = findViewById(R.id.price)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val selectedItem = intent.getSerializableExtra("product") as Product
        if (selectedItem != null) {
            if (selectedItem.title != null) {
                productTitle.text = selectedItem.title
            } else {
                productTitle.text = "Title information not available"
            }
            if (selectedItem.description != null) {
                productDescription.text = selectedItem.description
            } else {
                productDescription.text = "Description information not available"
            }
            if (selectedItem.price != null) {
                price.text = selectedItem.price.toString() +"$"
            }
            if (selectedItem.discountPercentage != null) {
                productDiscountPercentage.text = "Discount: " + selectedItem.discountPercentage.toString() + "%"
            } else{
                productDiscountPercentage.text = "Discount information not available"
            }
            if (selectedItem.stock != null) {
                productStock.text = "Stock: " + selectedItem.stock.toString()
            } else{
                productStock.text = "Stock information not available"
            }
            if (selectedItem.warrantyInformation != null) {
                productWarrantyInformation.text = "Warranty: " + selectedItem.warrantyInformation
            } else {
                productWarrantyInformation.text = "Warranty information not available"
            }
            if (selectedItem.shippingInformation != null) {
                productShippingInformation.text = "Shipping: " + selectedItem.shippingInformation
            } else{
                productShippingInformation.text = "Shipping information not available"
            }
            if (selectedItem.minimumOrderQuantity != null) {
                productMinimumOrderQuantity.text = "Minimum Order Quantity: " + selectedItem.minimumOrderQuantity.toString()
            } else {
                productMinimumOrderQuantity.text = "Minimum Order Quantity information not available"
            }
            if (selectedItem.rating != null ){
                productRating.rating = selectedItem.rating.toFloat()
            }
            if (selectedItem.images != null) {
                val imageAdapter = ImageAdapter(selectedItem.images)
                viewPager.adapter = imageAdapter

                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                }.attach()
            }
        }

        val isFavorite = favoriteDao.getFavoriteById(selectedItem.id, sharedPreferences.getUser()?.id!!) != null
        if (isFavorite) {
            likeButton.setImageTintList(ColorStateList.valueOf(Color.RED))
        } else {
            likeButton.setImageTintList(null)
        }

        val user = sharedPreferences.getUser()
        val userId = user?.id

        likeButton.setOnClickListener {
            val isCurrentlyFavorite = favoriteDao.getFavoriteById(selectedItem.id, userId!!) != null
            if (isCurrentlyFavorite != null) {
                if (isCurrentlyFavorite) {
                    val favorite = favoriteDao.getFavoriteById(selectedItem.id, userId)
                    if (favorite != null) {
                        favoriteDao.deleteFavorite(favorite)
                    }
                    likeButton.setImageTintList(null)
                    Toast.makeText(this, "Product removed from favorites", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val favorite = Favorite(
                        selectedItem.id,
                        userId,
                        selectedItem.title ?: "",
                        selectedItem.description ?: "",
                        selectedItem.price ?: 0.0,
                        selectedItem.discountPercentage ?: 0.0,
                        selectedItem.rating ?: 0.0,
                        selectedItem.stock ?: 0,
                        selectedItem.warrantyInformation ?: "",
                        selectedItem.shippingInformation ?: "",
                        selectedItem.returnPolicy?.toString() ?: "",
                        selectedItem.minimumOrderQuantity ?: 0,
                        selectedItem.images ?: listOf(),
                        selectedItem.thumbnail ?: ""
                    )
                    favoriteDao.insertFavorite(favorite)
                    likeButton.setImageTintList(ColorStateList.valueOf(Color.RED))
                    Toast.makeText(this, "Product added to favorites", Toast.LENGTH_SHORT).show()
                }
            }
        }

        addToCartButton.setOnClickListener {
            val addCartRequest = AddCartRequest(userId!!, listOf(CartItem(selectedItem.id, 1)))

            iDummyService = ApiClient.getClient().create(IDummyService::class.java)
            if (userId != null) {
                iDummyService.addToCart(addCartRequest).enqueue(object : Callback<Cart>{
                    override fun onResponse(p0: Call<Cart>, p1: Response<Cart>) {
                        if (p1.isSuccessful){
                            Toast.makeText(this@DetailActivity, "Thank you for your order", Toast.LENGTH_SHORT).show()
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
}