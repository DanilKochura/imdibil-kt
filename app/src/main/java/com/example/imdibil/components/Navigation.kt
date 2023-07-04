package com.example.imdibil.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.imdibil.components.models.BottomItem
import com.example.imdibil.ui.theme.TestTheme
import com.google.android.gms.common.SignInButton.ColorScheme

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

    BottomNavigation(backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.background ) {
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
                selectedContentColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                unselectedContentColor = androidx.compose.material3.MaterialTheme.colorScheme.tertiary

            )
        }
    }
}