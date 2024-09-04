package com.nexgen.fitnest.ui.Navigation

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.nexgen.fitnest.data.viewmodel.UserViewModel
import com.nexgen.fitnest.ui.screens.AboutUsScreen
import com.nexgen.fitnest.ui.screens.ContactUsScreen
import com.nexgen.fitnest.ui.screens.ExperienceScreen
import com.nexgen.fitnest.ui.screens.FeedbackScreen
import com.nexgen.fitnest.ui.screens.FitnessAssistantScreen
import com.nexgen.fitnest.ui.screens.HomeScreen
import com.nexgen.fitnest.ui.screens.LoginScreen
import com.nexgen.fitnest.ui.screens.PrivacyPolicyScreen
import com.nexgen.fitnest.ui.screens.ProfileScreen
import com.nexgen.fitnest.ui.screens.RateUsScreen
import com.nexgen.fitnest.ui.screens.RegisterScreen
import com.nexgen.fitnest.ui.screens.SettingsScreen
import com.nexgen.fitnest.ui.screens.SplashScreen
import com.nexgen.fitnest.ui.screens.TargetAreasScreen
import com.nexgen.fitnest.ui.screens.YourGoalsScreen
import com.nexgen.fitnest.ui.theme.FitnestTheme
import com.nexgen.fitnest.utilis.AdManager

@Composable
fun MyApp(navController: NavHostController, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val isRegistered = sharedPreferences.getBoolean("isRegistered", false)

    // Set the status bar color
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color(0xFF025D93),
            darkIcons = false
        )
    }

    // Function to show interstitial ad
    fun showInterstitialAd() {
        val activity = context as? Activity
        if (activity != null) {
            AdManager.showAd(activity)
        }
    }

    FitnestTheme {
        NavHost(navController = navController, startDestination = if (isRegistered) "home" else "splash") {
            composable("splash") {
                SplashScreen(navController = navController)
            }
            composable("yourgoals") {
                YourGoalsScreen(navController = navController)
            }
            composable("experience") {
                ExperienceScreen(navController = navController)
            }
            composable("targetareas") {
                TargetAreasScreen(navController = navController)
            }
            composable("register") {
                RegisterScreen(userViewModel = userViewModel, navController = navController)
            }
            composable("home") {
                HomeScreen(
                    onLogout = {
                        sharedPreferences.edit().putBoolean("isRegistered", false).apply()
                        navController.navigate("yourgoals") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    navController = navController
                )
            }
            composable("profile") {
                ProfileScreen(
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                        showInterstitialAd()
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    },
                    userViewModel = userViewModel, onNavigateBack = navController::popBackStack
                )
            }
            composable("settings") {
                SettingsScreen(navController = navController, userViewModel = userViewModel)
            }
            composable("aboutus") {
                AboutUsScreen(onBackClick = navController::popBackStack)
            }
            composable("login") {
                LoginScreen(navController = navController, userViewModel = userViewModel)
            }
            composable("privacypolicy") {
                PrivacyPolicyScreen(onBackClick = navController::popBackStack, navController = navController)
            }
            composable("feedback") {
                FeedbackScreen()
            }
            composable("contact_us") {
                ContactUsScreen(onBackClick = navController::popBackStack, navController = navController)
            }
            composable("fitness_assistant") { FitnessAssistantScreen(navController) }
            composable("rateus") {
                RateUsScreen(onBack = navController::popBackStack)
            }
        }
    }
}
