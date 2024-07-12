package com.example.afinal.models

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val image: String,
    val token: String,
    val refreshToken: String
)
