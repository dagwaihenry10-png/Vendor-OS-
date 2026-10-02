package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.NavyBg
import com.example.ui.theme.VendorOSTheme
import com.example.viewmodel.VendorOSViewModel

enum class AppDestination {
    ONBOARDING,
    AUTH,
    ROLE_SELECTION,
    MAIN
}

class MainActivity : ComponentActivity() {
    private val viewModel: VendorOSViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("vendoros_prefs", Context.MODE_PRIVATE)
        val initialOnboardingSeen = prefs.getBoolean("onboarding_seen", false)

        setContent {
            VendorOSTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = NavyBg
                ) {
                    val user by viewModel.user.collectAsStateWithLifecycle()

                    var currentScreen by remember {
                        mutableStateOf(
                            if (!initialOnboardingSeen) {
                                AppDestination.ONBOARDING
                            } else {
                                AppDestination.MAIN
                            }
                        )
                    }

                    when (currentScreen) {
                        AppDestination.ONBOARDING -> {
                            OnboardingScreen(
                                onFinished = {
                                    prefs.edit().putBoolean("onboarding_seen", true).apply()
                                    currentScreen = AppDestination.ROLE_SELECTION
                                }
                            )
                        }

                        AppDestination.AUTH -> {
                            AuthScreen(
                                onSignInSuccess = { name, email ->
                                    currentScreen = AppDestination.ROLE_SELECTION
                                }
                            )
                        }

                        AppDestination.ROLE_SELECTION -> {
                            BackHandler(enabled = initialOnboardingSeen) {
                                currentScreen = AppDestination.MAIN
                            }
                            RoleSelectionScreen(
                                currentMode = user.activeMode,
                                currentState = user.selectedState,
                                currentLga = user.selectedLGA,
                                currentArea = user.selectedArea,
                                currentBusinessName = user.businessName,
                                onConfirmed = { mode, state, lga, area, bizName ->
                                    viewModel.updateActiveMode(mode)
                                    viewModel.updateLocation(state, lga, area)
                                    currentScreen = AppDestination.MAIN
                                }
                            )
                        }

                        AppDestination.MAIN -> {
                            MainScreen(
                                viewModel = viewModel,
                                onOpenRoleSelection = {
                                    currentScreen = AppDestination.ROLE_SELECTION
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
