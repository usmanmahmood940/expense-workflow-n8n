package com.workflow.expense.ui

import android.R.style.Theme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.workflow.expense.ui.theme.Blue

private object Routes {
    const val SMS = "sms"
    const val LOG_EXPENSE = "log_expense"
    const val SETTINGS = "settings"
}

@Composable
fun HomeScreen() {
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(modifier = Modifier.drawBehind {
                val strokeWidth = 2.dp.toPx()
                drawLine(
                    color = Blue,
                    start = Offset(0f, 0f), // top-left
                    end = Offset(size.width, 0f), // top-right
                    strokeWidth = strokeWidth
                )
            }, containerColor = Color.White) {
                NavigationBarItem(
                    selected = currentRoute == Routes.SMS,
                    onClick = {
                        navController.navigate(Routes.SMS) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        val selected = currentRoute == Routes.SMS
                        Icon(
                            if (selected) Icons.Filled.Email else Icons.Outlined.Email,
                            contentDescription = null
                        )
                    },
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.LOG_EXPENSE,
                    onClick = {
                        navController.navigate(Routes.LOG_EXPENSE) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        val selected = currentRoute == Routes.LOG_EXPENSE
                        Icon(
                            if (selected) Icons.Filled.Edit else Icons.Outlined.Edit,
                            contentDescription = null
                        )
                    },
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent,
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.SETTINGS,
                    onClick = {
                        navController.navigate(Routes.SETTINGS) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        val selected = currentRoute == Routes.SETTINGS
                        Icon(
                            if (selected) Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = null
                        )
                    },
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SMS,
            modifier = Modifier
                .then(Modifier)
                .padding(innerPadding)
        ) {
            composable(Routes.SMS) {
                SmsScreen(
                )
            }
            composable(Routes.LOG_EXPENSE) {
                LogExpenseScreen()
            }
            composable(Routes.SETTINGS) {
                SettingsScreen()
            }
        }
    }
}


