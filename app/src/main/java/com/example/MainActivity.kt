package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.BetweenUsApp
import com.example.ui.BetweenUsViewModel
import com.example.ui.theme.BetweenUsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BetweenUsTheme {
                val viewModel: BetweenUsViewModel = viewModel()
                BetweenUsApp(viewModel = viewModel)
            }
        }
    }
}
