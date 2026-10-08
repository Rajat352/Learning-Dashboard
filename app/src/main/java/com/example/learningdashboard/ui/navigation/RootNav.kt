package com.example.learningdashboard.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.learningdashboard.ui.screens.courses.CoursesScreen
import com.example.learningdashboard.ui.screens.login.LoginScreen

@Composable
fun RootNav(
    modifier: Modifier = Modifier,
    startDestination: Route.TopLevel = Route.TopLevel.Login
) {

    val backStack = rememberNavBackStack(startDestination)

    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        NavDisplay(
            modifier = modifier.padding(paddingValues),
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Route.TopLevel.Login> {
                    LoginScreen(
                        onLoginSuccess = {
                            backStack.clear()
                            backStack.add(Route.TopLevel.Courses)
                        }
                    )
                }
                entry<Route.TopLevel.Courses> {
                    CoursesScreen()
                }
            }
        )
    }

}