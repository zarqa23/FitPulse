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

    fun getDirectLink(driveShareLink: String): String {
        val fileId = driveShareLink.substringAfter("/d/").substringBefore("/")
        return "https://drive.google.com/uc?id=$fileId"
    }


    fun convertVideoUrls(videoItems: List<VideoItem>): List<VideoItem> {
        return videoItems.map {
            VideoItem(getDirectLink(it.videoUrl), it.title)
        }
    }

    fun onBottomNavItemSelected(index: Int) {
        selectedItem.value = index
        when (index) {
            0 -> navController.navigate("profile") { popUpTo("home") { inclusive = true } }
            1 -> navController.navigate("home") { popUpTo("home") { inclusive = true } }
            2 -> navController.navigate("settings") { popUpTo("home") { inclusive = true } }
        }
    }
    val beginnerUpperBodyVideos = listOf(
        VideoItem("https://drive.google.com/file/d/1rj47hCt4kqhi1Ru47TXBU6tvLPw8MOB7/view?usp=sharing", "Push Ups"),
        VideoItem("https://drive.google.com/file/d/1auzg0IhTQqXSEZRRxr7XgnusMcsn011z/view?usp=sharing", "Jumping Jacks"),
        VideoItem("https://drive.google.com/file/d/1k7lWG3AslA9wElgWx3quGhbBOnvxoU6O/view?usp=sharing", "Squats"),
        VideoItem("https://drive.google.com/file/d/1-Hh_xdcw_bNLXvmrACd77aJuk8dIJ0PD/view?usp=sharing", "Lunges"),
        VideoItem("https://drive.google.com/file/d/1-Tc7vF3PjgokHHL-vYUIoblByq6JR4Uh/view?usp=sharing", "Plank"),
    )
    // Convert the URLs

    val beginnerLowerBodyVideos = listOf(
        VideoItem("https://drive.google.com/file/d/1IcQ1dt4Vzb8k-sC6gl2emYJ8Z3D8KS25/view?usp=sharing", "Burpees"),
        VideoItem("https://drive.google.com/file/d/1xErf728GZ5XleGdh5vEt6tzrh9roHYR9/view?usp=sharing", "Mountain Climbers"),
        VideoItem("https://drive.google.com/file/d/1IZxcVfZuWp4VbQPLyp4BKeWDSUPGojNg/view?usp=sharing", "High Knees"),
        VideoItem("https://drive.google.com/file/d/1xBqq1keVtoOfT-mUo5NReTeF1gGmVHy5/view?usp=sharing", "Bicycle Crunches"),
    )
    val beginnerFullBodyVideos = listOf(
        VideoItem("https://drive.google.com/file/d/1LhDUJih9rsr7D3ZeHGnuzXCEVhqAw3Z0/view?usp=sharing", "Pull Ups"),
        VideoItem("https://drive.google.com/file/d/1YEgVAFfLmqnJUsXEJ7QojVfi6EYXgkVH/view?usp=sharing", "Bench Press"),
        VideoItem("https://drive.google.com/file/d/1mwh9ByBkMz9Xxm7FP0sMMeMKn_30skBo/view?usp=sharing", "Tricep Dips"),
        VideoItem("https://drive.google.com/file/d/1f78X_eg2ddUnELCAW_w9wqHPFT0uhbOL/view?usp=sharing", "Leg Raises"),
        VideoItem("https://drive.google.com/file/d/1W1MZ0-wbuXuP_SQ9dkf8tQxSuKI31z0N/view?usp=sharing", "Russian Twists"),
    )
    val intermediateUpperBodyVideos = listOf(
        VideoItem("https://drive.google.com/file/d/1AhOQ1AxZ9IHy1alyIF4-zT02BWx1jQlK/view?usp=sharing", "Pull Ups"),
        VideoItem("https://drive.google.com/file/d/1gPh4S_u941yoj7OJnCcMSqmZUL1XUBcl/view?usp=sharing", "Bench Press"),
        VideoItem("https://drive.google.com/file/d/1W30k34vu-6awNO4gGlT3WR5ngrIiF2VE/view?usp=sharing", "Tricep Dips"),
        VideoItem("https://drive.google.com/file/d/1Yi5Tc9XxXn6ui-CGOPjRVD9ife7LSqET/view?usp=sharing", "Leg Raises"),
        VideoItem("https://drive.google.com/file/d/1UkG3Iq0vBnjrlBxTq9KdGL66aQaB9UgI/view?usp=sharing", "Russian Twists"),
        VideoItem("https://drive.google.com/file/d/1xcFpuIgxR3wXYsEbHtTYOtBGrJf9r9Vg/view?usp=sharing", "Side Plank"),
    )
    val intermediateLowerBodyVideos = listOf(
        VideoItem("https://drive.google.com/file/d/1HLOnEBToY97zhHiLOAn0LdAAB3jT18Pa/view?usp=sharing", "Pull Ups"),
        VideoItem("https://drive.google.com/file/d/1zhnJAQ4ryrVBCpJ9lmYLMZFQ0L8MPhcj/view?usp=sharing", "Bench Press"),
        VideoItem("https://drive.google.com/file/d/1SxNvnw7-Nb-RuaBL1rYw8Td9sg7BmIl1/view?usp=sharing", "Tricep Dips"),
    )
    val intermediateFullBodyVideos = listOf(
        VideoItem("https://drive.google.com/file/d/1szILog73uD5OTH4DQZrzNd8lT9nqirMc/view?usp=sharing", "Pull Ups"),
        VideoItem("https://drive.google.com/file/d/1UWshlt1bNCU6Z6_gJVjUrIeKRh7GZjIZ/view?usp=sharing", "Bench Press"),
        VideoItem("https://drive.google.com/file/d/1jwlKmYRNWzjPJnznByCmkM8cOQ5BMDkU/view?usp=sharing", "Tricep Dips"),
    )
    val expertUpperBodyVideos = listOf(
        VideoItem("https://drive.google.com/file/d/1NboXePVocVoqhol6q0zZIvZvbyJCVW_b/view?usp=sharing", "Pull Ups"),
        VideoItem("https://drive.google.com/file/d/1k7geHbJ7Q88EeeXjoBjnpsIdyS_vf-Td/view?usp=sharing", "Bench Press"),
        VideoItem("https://drive.google.com/file/d/1pGJaqdYVIctDhNUICHtS8PEKfPe3OOkJ/view?usp=sharing", "Tricep Dips"),
        VideoItem("https://drive.google.com/file/d/1cIG-JsJWk779_xgUlT_HU8L6A6rhniuY/view?usp=sharing", "Leg Raises"),
    )
    val expertLowerBodyVideos = listOf(
        VideoItem("https://drive.google.com/file/d/1uBlc5WbNyw9iKXZc6HTMEJsY0MOEBRqK/view?usp=sharing", "Pull Ups"),
        VideoItem("https://drive.google.com/file/d/1xoSqDeF3_SSkbNsf_tpLHeLZh6_KQXKx/view?usp=sharing", "Bench Press"),
        VideoItem("https://drive.google.com/file/d/16zOwsSp4tkm34HnWqUhJglbQuhoPHiji/view?usp=sharing", "Tricep Dips"),
    )
    val expertFullBodyVideos = listOf(
        VideoItem("https://drive.google.com/file/d/1aRwzsw5kDtDZlGkSeZxpwnYuB8u8rYUU/view?usp=sharing", "Pull Ups"),
        VideoItem("https://drive.google.com/file/d/1uc4h0Ku25gV8jj8JBYR9xaqU6bTUJt_y/view?usp=sharing", "Bench Press"),
        VideoItem("https://drive.google.com/file/d/1CTa8PgIBS43rD_ou0PYmaO0tAVXt7G4s/view?usp=sharing", "Tricep Dips"),
    )

    // Converted lists
    val convertedBeginnerUpperBodyVideos = convertVideoUrls(beginnerUpperBodyVideos)
    val convertedBeginnerLowerBodyVideos = convertVideoUrls(beginnerLowerBodyVideos)
    val convertedBeginnerFullBodyVideos = convertVideoUrls(beginnerFullBodyVideos)
    val convertedIntermediateUpperBodyVideos = convertVideoUrls(intermediateUpperBodyVideos)
    val convertedIntermediateLowerBodyVideos = convertVideoUrls(intermediateLowerBodyVideos)
    val convertedIntermediateFullBodyVideos = convertVideoUrls(intermediateFullBodyVideos)
    val convertedExpertUpperBodyVideos = convertVideoUrls(expertUpperBodyVideos)
    val convertedExpertLowerBodyVideos = convertVideoUrls(expertLowerBodyVideos)
    val convertedExpertFullBodyVideos = convertVideoUrls(expertFullBodyVideos)


    // Define video lists for each combination
    val videos = when {
        selectedExperience == "Beginner" && selectedArea == "Upper Body" -> convertedExpertFullBodyVideos
        selectedExperience == "Beginner" && selectedArea == "Lower Body" -> convertedBeginnerLowerBodyVideos
        selectedExperience == "Beginner" && selectedArea == "Full Body" -> convertedBeginnerFullBodyVideos
        selectedExperience == "Intermediate" && selectedArea == "Upper Body" -> convertedIntermediateUpperBodyVideos
        selectedExperience == "Intermediate" && selectedArea == "Lower Body" -> convertedIntermediateLowerBodyVideos
        selectedExperience == "Intermediate" && selectedArea == "Full Body" -> convertedIntermediateFullBodyVideos
        selectedExperience == "Advanced" && selectedArea == "Upper Body" -> convertedExpertUpperBodyVideos
        selectedExperience == "Advanced" && selectedArea == "Lower Body" -> convertedExpertLowerBodyVideos
        selectedExperience == "Advanced" && selectedArea == "Full Body" -> convertedExpertFullBodyVideos
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