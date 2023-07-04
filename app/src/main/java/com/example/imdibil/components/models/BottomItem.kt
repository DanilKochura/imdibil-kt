package com.example.imdibil.components.models

import com.example.imdibil.R

sealed class BottomItem(val title: String, val icon: Int, val route: String)
{
    object  Home: BottomItem("Главная", R.drawable.baseline_home_24, "home")
    object  News: BottomItem("Тройки", R.drawable.baseline_movie_creation_24, "news")
    object  Profile: BottomItem("Профиль", R.drawable.baseline_person_24, "profile")
    object  Notifications: BottomItem("Уведомления", R.drawable.baseline_notifications_24, "notifications")
}
