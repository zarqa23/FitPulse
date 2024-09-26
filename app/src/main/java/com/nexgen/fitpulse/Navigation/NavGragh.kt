package com.nexgen.fitpulse.Navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nexgen.fitpulse.ui.screens.AboutUsScreen
import com.nexgen.fitpulse.ui.screens.AdvancedDay1DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.AdvancedDay1WorkoutScreen
import com.nexgen.fitpulse.ui.screens.AdvancedDay2DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.AdvancedDay2WorkoutScreen
import com.nexgen.fitpulse.ui.screens.AdvancedDay3DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.AdvancedDay3WorkoutScreen
import com.nexgen.fitpulse.ui.screens.AdvancedDay4DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.AdvancedDay4WorkoutScreen
import com.nexgen.fitpulse.ui.screens.AdvancedDay5DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.AdvancedDay5WorkoutScreen
import com.nexgen.fitpulse.ui.screens.AdvancedDay6DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.AdvancedDay6WorkoutScreen
import com.nexgen.fitpulse.ui.screens.AdvancedDay7DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.AdvancedDay7WorkoutScreen
import com.nexgen.fitpulse.ui.screens.BeginnerDay1DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.BeginnerDay1WorkoutScreen
import com.nexgen.fitpulse.ui.screens.BeginnerDay2DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.BeginnerDay2WorkoutScreen
import com.nexgen.fitpulse.ui.screens.BeginnerDay3DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.BeginnerDay3WorkoutScreen
import com.nexgen.fitpulse.ui.screens.BeginnerDay4DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.BeginnerDay4WorkoutScreen
import com.nexgen.fitpulse.ui.screens.BeginnerDay5DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.BeginnerDay5WorkoutScreen
import com.nexgen.fitpulse.ui.screens.BeginnerDay6DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.BeginnerDay6WorkoutScreen
import com.nexgen.fitpulse.ui.screens.BeginnerDay7DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.BeginnerDay7WorkoutScreen
import com.nexgen.fitpulse.ui.screens.ConsultantScreen
import com.nexgen.fitpulse.ui.screens.ConsultantScreen2
import com.nexgen.fitpulse.ui.screens.ContactUsScreen
import com.nexgen.fitpulse.ui.screens.FitnessAssistantScreen
import com.nexgen.fitpulse.ui.screens.Google_Profile_Screen
import com.nexgen.fitpulse.ui.screens.HomeScreen
import com.nexgen.fitpulse.ui.screens.IntermediateDay1DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.IntermediateDay1WorkoutScreen
import com.nexgen.fitpulse.ui.screens.IntermediateDay2DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.IntermediateDay2WorkoutScreen
import com.nexgen.fitpulse.ui.screens.IntermediateDay3DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.IntermediateDay3WorkoutScreen
import com.nexgen.fitpulse.ui.screens.IntermediateDay4DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.IntermediateDay4WorkoutScreen
import com.nexgen.fitpulse.ui.screens.IntermediateDay5DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.IntermediateDay5WorkoutScreen
import com.nexgen.fitpulse.ui.screens.IntermediateDay6DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.IntermediateDay6WorkoutScreen
import com.nexgen.fitpulse.ui.screens.IntermediateDay7DetailedWorkoutScreen
import com.nexgen.fitpulse.ui.screens.IntermediateDay7WorkoutScreen
import com.nexgen.fitpulse.ui.screens.LoginScreen
import com.nexgen.fitpulse.ui.screens.Phone_Number_Profile_Screen
import com.nexgen.fitpulse.ui.screens.PlansScreen
import com.nexgen.fitpulse.ui.screens.PrivacyPolicyScreen
import com.nexgen.fitpulse.ui.screens.ProfileScreen
import com.nexgen.fitpulse.ui.screens.RegistrationScreen
import com.nexgen.fitpulse.ui.screens.SettingsScreen
import com.nexgen.fitpulse.ui.screens.YourGoalsScreen
import com.nexgen.fitpulse.ui.screens.SplashScreen
import com.nexgen.fitpulse.ui.screens.WorkoutScreen
import com.nexgen.fitpulse.ui.screens.YourTargetAreaScreen
import com.nexgen.fitpulse.ui.screens.YourExperienceScreen
import com.nexgen.fitpulse.ui.viewmodel.PreferencesUtil
import com.nexgen.fitpulse.ui.viewmodel.UserSelectionViewModel

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel : UserSelectionViewModel = viewModel()
    val isLoggedIn = PreferencesUtil.getLoginState(context)
    val selectedItem = remember { mutableStateOf(0) } // Default to home screen
    val onItemSelected: (Int) -> Unit = { itemIndex ->
        selectedItem.value = itemIndex
        when (itemIndex) {
            0 -> navController.navigate("home") { popUpTo("home") { inclusive = true } }
            1 -> navController.navigate("workout") { popUpTo("workout") { inclusive = true } }
            2 -> navController.navigate("plans") { popUpTo("plans") { inclusive = true } }
            3 -> navController.navigate("settings") { popUpTo("settings") { inclusive = true } }
        }
    }
    NavHost(navController = navController, startDestination = if (isLoggedIn) "home" else "your_goals",) {

        composable("your_goals") { YourGoalsScreen(navController, viewModel) }
        composable("your_experience") { YourExperienceScreen(navController, viewModel) }
        composable("your_target_area") { YourTargetAreaScreen(navController, viewModel) }
        composable("registration") { RegistrationScreen(navController, viewModel) }
        composable("login") { LoginScreen(navController , viewModel) }
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = viewModel,
                selectedItem = selectedItem.value,
                onItemSelected = onItemSelected
            )
        }
        composable("consultant_screen") { ConsultantScreen(navController) }
        composable("consultant_screen2") { ConsultantScreen2(navController) }
        composable("google_profile_screen/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            Google_Profile_Screen(userId = userId, navController = navController , viewModel)
        }
        composable("phone_number_profile_screen"){
            Phone_Number_Profile_Screen(navController , viewModel)
        }
        composable("workout") {
            WorkoutScreen(
                selectedItem = selectedItem.value,
                onItemSelected = onItemSelected,
                onBackClick = { navController.popBackStack() },
                viewModel,
                navController
            )
        }
        composable("plans") {
            PlansScreen(
                selectedItem = selectedItem.value,
                onItemSelected = onItemSelected,
                onBackClick = { navController.popBackStack()},
                viewModel,navController
            )
        }
        composable("settings") {
            SettingsScreen(
                selectedItem = selectedItem.value,
                onItemSelected = onItemSelected,
                onBackClick = { navController.popBackStack() },
                navController,
                viewModel
            )
        }
        composable("contact_us"){
            ContactUsScreen(onBackClick = { navController.popBackStack() } , navController)
        }
        composable("privacy_policy"){
            PrivacyPolicyScreen(onBackClick = { navController.popBackStack() } , navController)
        }
        composable("profile"){
            ProfileScreen(navController , onBackClick = { navController.navigate("home")  } , viewModel)
        }
        composable("beg_day1_workout"){
            BeginnerDay1WorkoutScreen(navController)
        }
        composable("beg_day2_workout"){
            BeginnerDay2WorkoutScreen(navController)
        }
        composable("beg_day3_workout"){
            BeginnerDay3WorkoutScreen(navController)
        }
        composable("beg_day4_workout"){
            BeginnerDay4WorkoutScreen(navController)
        }
        composable("beg_day5_workout"){
            BeginnerDay5WorkoutScreen(navController)
        }
        composable("beg_day6_workout"){
            BeginnerDay6WorkoutScreen(navController)
        }
        composable("beg_day7_workout"){
            BeginnerDay7WorkoutScreen(navController)
        }
        composable("int_day1_workout"){
            IntermediateDay1WorkoutScreen(navController)
        }
        composable("int_day2_workout"){
            IntermediateDay2WorkoutScreen(navController)
        }
        composable("int_day3_workout"){
            IntermediateDay3WorkoutScreen(navController)
        }
        composable("int_day4_workout"){
            IntermediateDay4WorkoutScreen(navController)
        }
        composable("int_day5_workout"){
            IntermediateDay5WorkoutScreen(navController)
        }
        composable("int_day6_workout"){
            IntermediateDay6WorkoutScreen(navController)
        }
        composable("int_day7_workout"){
            IntermediateDay7WorkoutScreen(navController)
        }
        composable("adv_day1_workout"){
            AdvancedDay1WorkoutScreen(navController)
        }
        composable("adv_day2_workout"){
            AdvancedDay2WorkoutScreen(navController)
        }
        composable("adv_day3_workout") {
            AdvancedDay3WorkoutScreen(navController)
        }
        composable("adv_day4_workout"){
            AdvancedDay4WorkoutScreen(navController)
        }
        composable("adv_day5_workout"){
            AdvancedDay5WorkoutScreen(navController)
        }
        composable("adv_day6_workout"){
            AdvancedDay6WorkoutScreen(navController)
        }
        composable("adv_day7_workout"){
            AdvancedDay7WorkoutScreen(navController)
        }
        composable("beg_detailed_day_1"){
            BeginnerDay1DetailedWorkoutScreen(navController)
        }
        composable("beg_detailed_day_2"){
            BeginnerDay2DetailedWorkoutScreen(navController)
        }
        composable("beg_detailed_day_3"){
            BeginnerDay3DetailedWorkoutScreen(navController)
        }
        composable("beg_detailed_day_4"){
            BeginnerDay4DetailedWorkoutScreen(navController)
        }
        composable("beg_detailed_day_5"){
            BeginnerDay5DetailedWorkoutScreen(navController)
        }
        composable("beg_detailed_day_6"){
            BeginnerDay6DetailedWorkoutScreen(navController)
        }
        composable("beg_detailed_day_7"){
            BeginnerDay7DetailedWorkoutScreen(navController)
        }
        composable("int_detailed_day_1"){
            IntermediateDay1DetailedWorkoutScreen(navController)
        }
        composable("int_detailed_day_2"){
            IntermediateDay2DetailedWorkoutScreen(navController)
        }
        composable("int_detailed_day_3"){
            IntermediateDay3DetailedWorkoutScreen(navController)
        }
        composable("int_detailed_day_4"){
            IntermediateDay4DetailedWorkoutScreen(navController)
        }
        composable("int_detailed_day_5"){
            IntermediateDay5DetailedWorkoutScreen(navController)
        }
        composable("int_detailed_day_6"){
            IntermediateDay6DetailedWorkoutScreen(navController)
        }
        composable("int_detailed_day_7"){
            IntermediateDay7DetailedWorkoutScreen(navController)
        }
        composable("adv_detailed_day_1"){
            AdvancedDay1DetailedWorkoutScreen(navController)
        }
        composable("adv_detailed_day_2"){
            AdvancedDay2DetailedWorkoutScreen(navController)
        }
        composable("adv_detailed_day_3"){
            AdvancedDay3DetailedWorkoutScreen(navController)
        }
        composable("adv_detailed_day_4"){
            AdvancedDay4DetailedWorkoutScreen(navController)
        }
        composable("adv_detailed_day_5"){
            AdvancedDay5DetailedWorkoutScreen(navController)
        }
        composable("adv_detailed_day_6"){
            AdvancedDay6DetailedWorkoutScreen(navController)
        }
        composable("adv_detailed_day_7"){
            AdvancedDay7DetailedWorkoutScreen(navController)
        }
        composable("fitness_assistant") {
            FitnessAssistantScreen(navController)
        }
        composable("about_us"){
            AboutUsScreen(onBackClick = { navController.popBackStack() } ,navController)
        }
    }
}

