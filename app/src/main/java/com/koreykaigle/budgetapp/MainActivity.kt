package com.koreykaigle.budgetapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.koreykaigle.budgetapp.ui.navigation.BudgetNavHost
import com.koreykaigle.budgetapp.ui.theme.BudgetAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BudgetAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BudgetNavHost()
                }
            }
        }
    }
}
