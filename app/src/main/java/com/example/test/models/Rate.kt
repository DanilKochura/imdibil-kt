package com.example.test.models

data class Rate(
    val rate: Int,
    val author: User? = null,
    val movie_name: String? = null,
)
