package com.example.afinal.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.afinal.models.User
import com.google.gson.Gson

class SharedPref(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    private val gson = Gson()

    companion object {
        private const val IS_LOGGED_IN = "is_logged_in"
        private const val JWT_TOKEN = "jwt_token"
        private const val USER = "user"
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false)
    }

    fun setJwtToken(token: String) {
        editor.putString(JWT_TOKEN, token).apply()
    }

    fun getJwtToken(): String? {
        return sharedPreferences.getString(JWT_TOKEN, null)
    }

    fun setUser(user: User) {
        val userJson = gson.toJson(user)
        editor.putString(USER, userJson).apply()
    }

    fun getUser(): User? {
        val userJson = sharedPreferences.getString(USER, null)
        return gson.fromJson(userJson, User::class.java)
    }

    fun clear() {
        editor.clear().apply()
    }
}