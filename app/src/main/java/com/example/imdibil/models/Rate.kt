package com.example.imdibil.models

data class Rate(
    val rate: Int,
    val author: User? = null,
    val movie_name: String? = null,
    val movie_poster: String? = null,
)
