package com.domingame.staduapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModelProvider
import com.domingame.staduapp.ui.MainViewModel
import com.domingame.staduapp.ui.screens.MainScreen
import com.domingame.staduapp.ui.theme.StaduAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as StaduApp
        val viewModel = ViewModelProvider(this, app.viewModelFactory)[MainViewModel::class.java]
        
        setContent {
            StaduAppTheme {
                Surface(
                    modifier = androidx.compose.foundation.layout.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}
