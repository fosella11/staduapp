package com.domingame.staduapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.domingame.staduapp.core.di.ServiceLocator
import com.domingame.staduapp.feature.stadium.presentation.MainViewModel
import com.domingame.staduapp.feature.stadium.presentation.StadiumScreen
import com.domingame.staduapp.ui.theme.StaduAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val viewModel = ViewModelProvider(this, ServiceLocator.mainViewModelFactory)[MainViewModel::class.java]
        
        setContent {
            StaduAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StadiumScreen(viewModel)
                }
            }
        }
    }
}
