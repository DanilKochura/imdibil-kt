package com.example.test.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.test.components.models.BottomItem

@Composable
fun BottomNavigationMenu(
    navController: NavController
)
{
    val list = listOf(
        BottomItem.Home,
        BottomItem.News,
        BottomItem.Profile,
        BottomItem.Notifications
    )

    BottomNavigation( backgroundColor = Color.White) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        list.forEach { item ->
            BottomNavigationItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = "Icon"
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 9.sp
                    )
                },
                selectedContentColor = Color.DarkGray,
                unselectedContentColor = Color.Gray
            )
        }
    }
}