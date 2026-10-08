package com.example.learningdashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learningdashboard.ui.screens.MainViewModel
import com.example.learningdashboard.ui.navigation.RootNav
import com.example.learningdashboard.ui.navigation.Route
import com.example.learningdashboard.ui.screens.AuthState
import com.example.learningdashboard.ui.theme.LearningDashboardTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val authState by viewModel.authState.collectAsStateWithLifecycle()

            LearningDashboardTheme {
                when(val state = authState) {
                    AuthState.Loading -> {}
                    AuthState.Unauthenticated, AuthState.Error -> {
                        RootNav(startDestination = Route.TopLevel.Login)
                    }

                    is AuthState.Authenticated -> {
                        key(state.user.userId) {
                            RootNav(startDestination = Route.TopLevel.Courses)
                        }
                    }
                }
            }
        }
    }
}