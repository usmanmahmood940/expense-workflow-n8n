package com.workflow.expense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.workflow.expense.ui.SmsScreen
import com.workflow.expense.ui.theme.ExpenseWorkflowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseWorkflowTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    SmsScreen()
                }
            }
        }
    }
}