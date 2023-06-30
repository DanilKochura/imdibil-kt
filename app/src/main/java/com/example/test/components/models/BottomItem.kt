package com.example.test.components.models

import com.example.test.R

sealed class BottomItem(val title: String, val icon: Int, val route: String)
{
    object  Study: BottomItem("Обучение", R.drawable.baseline_school_24, "study")
    object  Catalog: BottomItem("Каталог", R.drawable.baseline_shopping_basket_24, "catalog")
    object  Profile: BottomItem("Профиль", R.drawable.baseline_person_24, "profile")
    object  Notifications: BottomItem("Уведомления", R.drawable.baseline_notifications_24, "notifications")
}
