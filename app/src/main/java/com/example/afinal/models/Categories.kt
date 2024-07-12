package com.example.afinal.models

typealias CategoryList = ArrayList<Categories>

data class Categories (
    val slug: String,
    val name: String,
    val url: String
)
