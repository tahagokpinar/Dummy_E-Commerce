package com.example.afinal.services

import com.example.afinal.models.AddCartRequest
import com.example.afinal.models.Cart
import com.example.afinal.models.Carts
import com.example.afinal.models.CategoryList
import com.example.afinal.models.LoginRequest
import com.example.afinal.models.LoginResponse
import com.example.afinal.models.Products
import com.example.afinal.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface IDummyService {
    @GET("products")
    fun getProducts(): Call<Products>

    @GET("products/categories")
    fun getCategories(): Call<CategoryList>

    @GET("products/category/{category}")
    fun getProductsByCategory(@Path("category") category: String): Call<Products>

    @GET("products/search")
    fun getProductsBySearch(@Query("q") query: String): Call<Products>

    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("auth/me")
    fun getProfile(@Header("Authorization") token: String): Call<User>

    @PATCH("users/{userId}")
    fun updateProfile(
        @Header("Authorization") authHeader: String,
        @Path("userId") userId: Long,
        @Body updatedFields: HashMap<String, Any>
    ): Call<User>

    @GET("users/{userId}/carts")
    fun getCarts(@Path("userId") userId: Long): Call<Carts>

    @POST("carts/add")
    fun addToCart(@Body addCartRequest: AddCartRequest): Call<Cart>

}