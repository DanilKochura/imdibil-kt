package com.example.imdibil.models

data class Movie(
    val id: Int,
    val name: String,
    val orginal: String,
    val year: Int,
    val duration: Int,
    val genres: String,
    val director: String,
    val exp_rates: List<Rate>,
    val image: String,
    val imdb: Double,
    val kp: Double,
    val our: Double?,
    val url: String,
    val positions: List<Int>? = null,
    val id_e: Int? = null,
    val descr: String? = null


)
