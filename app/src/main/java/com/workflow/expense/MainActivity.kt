package com.workflow.expense

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.workflow.expense.ui.HomeScreen
import com.workflow.expense.ui.theme.ExpenseWorkflowTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseWorkflowTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).fillMaxSize()) {
                    HomeScreen()
                }
            }
        }
    }
}