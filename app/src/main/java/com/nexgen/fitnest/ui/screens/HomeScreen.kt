package com.nexgen.fitnest.ui.screens

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nexgen.fitnest.R
import com.nexgen.fitnest.data.models.VideoItem
import com.nexgen.fitnest.ui.components.AppBackground
import com.nexgen.fitnest.ui.components.BannerAdView
import com.nexgen.fitnest.ui.components.BottomNavigationBar
import com.nexgen.fitnest.ui.components.CustomText
import com.nexgen.fitnest.ui.components.DrawerContent
import com.nexgen.fitnest.ui.components.LogoutDialog
import com.nexgen.fitnest.ui.components.VideoCard
import com.nexgen.fitnest.utilis.AdManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onLogout: () -> Unit, navController: NavHostController) {
    var context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val scope = rememberCoroutineScope()
    val showLogoutDialog = remember { mutableStateOf(false) }
    val selectedItem = remember { mutableStateOf(1) }
    val selectedArea = sharedPreferences.getString("selectedArea", "Not Selected")
    val selectedGoal = sharedPreferences.getString("selectedGoal", "Not Selected")
    val selectedExperience = sharedPreferences.getString("selectedExperience", "Not Selected")
    fun onBottomNavItemSelected(index: Int) {
        selectedItem.value = index
        when (index) {
            0 -> navController.navigate("profile") { popUpTo("home") { inclusive = true } }
            1 -> navController.navigate("home") { popUpTo("home") { inclusive = true } }
            2 -> navController.navigate("settings") { popUpTo("home") { inclusive = true } }
        }
    }
    val beginnerUpperBodyVideos = listOf(
        VideoItem(R.raw.beg_up1, "Push Ups"),
        VideoItem(R.raw.beg_up2, "Jumping Jacks"),
        VideoItem(R.raw.beg_up3, "Squats"),
        VideoItem(R.raw.beg_up4, "Lunges"),
        VideoItem(R.raw.beg_up5, "Plank"),
    )
    val beginnerLowerBodyVideos = listOf(
        VideoItem(R.raw.beginner_lower1, "Burpees"),
        VideoItem(R.raw.begginer_lower2, "Mountain Climbers"),
        VideoItem(R.raw.beginer_lower3, "High Knees"),
        VideoItem(R.raw.beginner_lower4, "Bicycle Crunches"),
    )
    val beginnerFullBodyVideos = listOf(
        VideoItem(R.raw.beg_full1, "Pull Ups"),
        VideoItem(R.raw.beg_full2, "Bench Press"),
        VideoItem(R.raw.beg_full3, "Tricep Dips"),
        VideoItem(R.raw.beg_full4, "Leg Raises"),
        VideoItem(R.raw.beg_full5, "Russian Twists"),
    )
    val intermediateUpperBodyVideos = listOf(
        VideoItem(R.raw.int_up1, "Pull Ups"),
        VideoItem(R.raw.int_up2, "Bench Press"),
        VideoItem(R.raw.int_up3, "Tricep Dips"),
        VideoItem(R.raw.int_up4, "Leg Raises"),
        VideoItem(R.raw.int_up5, "Russian Twists"),
        VideoItem(R.raw.int_up6, "Side Plank"),
    )
    val intermediateLowerBodyVideos = listOf(
        VideoItem(R.raw.int_low1, "Pull Ups"),
        VideoItem(R.raw.int_low2, "Bench Press"),
        VideoItem(R.raw.int_low3, "Tricep Dips"),
    )
    val intermediateFullBodyVideos = listOf(
        VideoItem(R.raw.int_full1, "Pull Ups"),
        VideoItem(R.raw.int_full2, "Bench Press"),
        VideoItem(R.raw.int_full3, "Tricep Dips"),
    )
    val expertUpperBodyVideos = listOf(
        VideoItem(R.raw.exp_up1, "Pull Ups"),
        VideoItem(R.raw.exp_up2, "Bench Press"),
        VideoItem(R.raw.exp_up3, "Tricep Dips"),
        VideoItem(R.raw.exp_up4, "Leg Raises"),
    )
    val expertLowerBodyVideos = listOf(
        VideoItem(R.raw.exp_low1, "Pull Ups"),
        VideoItem(R.raw.exp_low2, "Bench Press"),
        VideoItem(R.raw.exp_low3, "Tricep Dips"),
    )
    val expertFullBodyVideos = listOf(
        VideoItem(R.raw.exp_full1, "Pull Ups"),
        VideoItem(R.raw.exp_full2, "Bench Press"),
        VideoItem(R.raw.exp_full3, "Tricep Dips"),
    )

    // Define video lists for each combination
    val videos = when {
        selectedExperience == "Beginner" && selectedArea == "Upper Body" -> beginnerUpperBodyVideos
        selectedExperience == "Beginner" && selectedArea == "Lower Body" -> beginnerLowerBodyVideos
        selectedExperience == "Beginner" && selectedArea == "Full Body" -> beginnerFullBodyVideos
        selectedExperience == "Intermediate" && selectedArea == "Upper Body" -> intermediateUpperBodyVideos
        selectedExperience == "Intermediate" && selectedArea == "Lower Body" -> intermediateLowerBodyVideos
        selectedExperience == "Intermediate" && selectedArea == "Full Body" -> intermediateFullBodyVideos
        selectedExperience == "Advanced" && selectedArea == "Upper Body" -> expertUpperBodyVideos
        selectedExperience == "Advanced" && selectedArea == "Lower Body" -> expertLowerBodyVideos
        selectedExperience == "Advanced" && selectedArea == "Full Body" -> expertFullBodyVideos
        // Add more conditions for other combinations
        else -> emptyList() // Default to empty list if no match
    }

    // Function to show interstitial ad
    fun showInterstitialAd() {
        val activity = context as? Activity
        if (activity != null) {
            AdManager.showAd(activity)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .width(200.dp) // Adjust width as needed
                    .height(400.dp) // Adjust height as needed
                    .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                    .background(Color(0xFFFFFFF7)) // Match background color with DrawerContent
            ) {
                ModalDrawerSheet {
                    DrawerContent(navController, onLogout)
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { CustomText(text = "", fontSize = 24.sp, fontFamily = FontFamily(Font(R.font.robotomedium))) },
                    navigationIcon = {
                        IconButton(onClick = {
                            showInterstitialAd()
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    ),
                    actions = {
                        IconButton(onClick = { navController.navigate("fitness_assistant") }) {
                            Image(painter = painterResource(id = R.drawable.ai_icon), contentDescription = "Notifications")
                        }
                    }
                )
            },
            bottomBar = {
                Column {
                    // Banner Ad view
                    BannerAdView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp),
                        adUnitId = "ca-app-pub-3940256099942544/6300978111" // Replace with your actual Ad Unit ID
                    )
                    BottomNavigationBar(
                        selectedItem = selectedItem.value,
                        onItemSelected = { onBottomNavItemSelected(it) }
                    )
                }
            }
        ) { innerPadding ->
            AppBackground {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    // User goals card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(30.dp))
                            .padding(start = 16.dp, end = 16.dp)
                            .height(300.dp) // Increased height for more content
                            .padding(bottom = 16.dp),
                        elevation = CardDefaults.cardElevation(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF025D93))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Row for muscle icon and "Muscle Gain" text
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Icon for muscle (replace with actual muscle icon)
                                Text(
                                    text = selectedGoal.toString(),
                                    fontSize = 24.sp,
                                    fontFamily = FontFamily(Font(R.font.robotomedium)),
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.vector), // Replace with your muscle icon
                                    contentDescription = "Muscle Icon",
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            // Text for "Time Period"
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = "Time Period",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.robotomedium)),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(start = 12.dp, top = 8.dp)
                                )
                                Text(text = selectedExperience.toString(), fontSize = 16.sp
                                    , modifier = Modifier.padding(end = 48.dp, top = 8.dp)
                                    , color = Color.Transparent,
                                    )
                            }

                            // Circular Box for "12 weeks"
                            Box(
                                modifier = Modifier
                                    .size(100.dp) // Circular shape with 100.dp diameter
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 2.dp)
                                    .background(color = Color.White, shape = CircleShape), // CircleShape used here
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(text = "12", fontSize = 24.sp, color = Color(0xFF025D93), fontFamily = FontFamily(
                                        Font(R.font.rumraisinregular)
                                    )
                                    )
                                    Text(text = "Weeks", fontSize = 24.sp, color = Color(0xFF025D93), fontFamily = FontFamily(
                                        Font(R.font.rumraisinregular)
                                    )
                                    )
                                }
                            }

                            // Text for "Build Strength and Muscle"
                            Text(
                                text = selectedArea.toString(),
                                fontSize = 18.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily(Font(R.font.robotomedium))
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(videos) { videoItem ->
                            VideoCard(videoItem)
                        }
                    }
                }
            }
        }

        // Logout dialog
        if (showLogoutDialog.value) {
            LogoutDialog(onDismiss = { showLogoutDialog.value = false }, onLogout = onLogout)
        }
    }
}