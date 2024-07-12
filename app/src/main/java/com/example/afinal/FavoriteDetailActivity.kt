package com.example.afinal

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.example.afinal.adapters.ImageAdapter
import com.example.afinal.configs.ApiClient
import com.example.afinal.configs.AppDatabase
import com.example.afinal.entities.Favorite
import com.example.afinal.models.AddCartRequest
import com.example.afinal.models.Cart
import com.example.afinal.models.CartItem
import com.example.afinal.services.IDummyService
import com.example.afinal.utils.SharedPref
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteDetailActivity : AppCompatActivity() {

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
    lateinit var price: TextView

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

        setContentView(R.layout.activity_favorite_detail)

        viewPager = findViewById(R.id.f_productImagesViewPager)
        likeButton = findViewById(R.id.f_LikeButton)
        addToCartButton = findViewById(R.id.f_addToCartButton)
        productRating = findViewById(R.id.f_productRating)
        productTitle = findViewById(R.id.f_productTitle)
        productDescription = findViewById(R.id.f_productDescription)
        productDiscountPercentage = findViewById(R.id.f_productDiscountPercentage)
        productStock = findViewById(R.id.f_productStock)
        productWarrantyInformation = findViewById(R.id.f_productWarrantyInformation)
        productShippingInformation = findViewById(R.id.f_productShippingInformation)
        productMinimumOrderQuantity = findViewById(R.id.f_productMinimumOrderQuantity)
        tabLayout = findViewById(R.id.f_tabLayout)
        price = findViewById(R.id.f_price)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val favorite = intent.getSerializableExtra("favorite") as Favorite?

        if (favorite != null) {
            productTitle.text = favorite.p_title
            productDescription.text = favorite.p_description
            price.text = "${favorite.p_price}$"
            productDiscountPercentage.text = "Discount: ${favorite.p_discountPercentage}%"
            productStock.text = "Stock: ${favorite.p_stock}"
            productWarrantyInformation.text = "Warranty: ${favorite.p_warrantyInformation}"
            productShippingInformation.text = "Shipping: ${favorite.p_shippingInformation}"
            productMinimumOrderQuantity.text = "Minimum Order Quantity: ${favorite.p_minimumOrderQuantity}"
            productRating.rating = favorite.p_rating.toFloat()

            val imageAdapter = ImageAdapter(favorite.p_images)
            viewPager.adapter = imageAdapter

            TabLayoutMediator(tabLayout, viewPager) { tab, position -> }.attach()
        }

        val user = sharedPreferences.getUser()
        val userId = user?.id

        val isFavorite = favoriteDao.getFavoriteById(favorite?.p_id ?: 0, userId!!) != null
        if (isFavorite) {
            likeButton.setImageTintList(ColorStateList.valueOf(Color.RED))
        } else {
            likeButton.setImageTintList(null)
        }

        likeButton.setOnClickListener {
            val isCurrentlyFavorite = favoriteDao.getFavoriteById(favorite?.p_id ?: 0, userId!!) != null
            if (isCurrentlyFavorite != null) {
                if (isCurrentlyFavorite) {
                    val favoriteItem = favoriteDao.getFavoriteById(favorite?.p_id ?: 0, userId)
                    if (favoriteItem != null) {
                        favoriteDao.deleteFavorite(favoriteItem)
                    }
                    likeButton.setImageTintList(null)
                    Toast.makeText(this, "Product removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    favorite?.let {
                        favoriteDao.insertFavorite(it)
                    }
                    likeButton.setImageTintList(ColorStateList.valueOf(Color.RED))
                    Toast.makeText(this, "Product added to favorites", Toast.LENGTH_SHORT).show()
                }
            }
        }

        addToCartButton.setOnClickListener {
            favorite?.let {
                val addCartRequest = AddCartRequest(userId!!, listOf(CartItem(it.p_id, 1)))

                iDummyService = ApiClient.getClient().create(IDummyService::class.java)
                if (userId != null) {
                    iDummyService.addToCart(addCartRequest).enqueue(object : Callback<Cart> {
                        override fun onResponse(p0: Call<Cart>, p1: Response<Cart>) {
                            if (p1.isSuccessful) {
                                Toast.makeText(this@FavoriteDetailActivity, "Thank you for your order", Toast.LENGTH_SHORT).show()
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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}