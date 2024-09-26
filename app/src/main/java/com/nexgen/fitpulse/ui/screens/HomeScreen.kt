package com.nexgen.fitpulse.ui.screens

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import com.nexgen.fitpulse.AdManager
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.nexgen.fitpulse.R
import com.google.android.exoplayer2.Player
import com.google.firebase.auth.FirebaseAuth
import com.nexgen.fitpulse.ui.components.BannerAdView
import com.nexgen.fitpulse.ui.viewmodel.UserSelectionViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: UserSelectionViewModel, selectedItem: Int, onItemSelected: (Int) -> Unit) {
    var context = LocalContext.current
    var showDialog2 by remember { mutableStateOf(false) }
    // State variables for stored values
    val experience = viewModel.selectedExperience.value ?: "Not selected"
    val goal = viewModel.selectedGoal.value ?: "Not selected"
    val targetArea = viewModel.selectedTargetArea.value ?: "Not selected"
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val firstName = sharedPreferences.getString("firstName", "Guest")
    val lastName = sharedPreferences.getString("lastName", "")
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isLoggedIn = viewModel.isLoggedIn.value // Assuming you have a state for login status
    // State for showing dialog
    var showDialog by remember { mutableStateOf(false) }
    // Determine current day
    val phoneNumber = sharedPreferences.getString("phone_number", "No phone number")
    val currentDay = remember { getCurrentDay() }
    var isSkipped = viewModel.isSkipLoggedIn.value


    // Function to show interstitial ad
    fun showInterstitialAd() {
        val activity = context as? Activity
        if (activity != null) {
            AdManager.showAd(activity)
        }
    }

    // Function to show dialog if not logged in
    fun checkLoginStatusAndShowDialog(action: () -> Unit) {
        if (isSkipped) {
            action()
        } else {
            showDialog = true // Show dialog if not logged in
        }
    }


    // Example of tracking access per day
    val accessTracker = remember { mutableStateOf(
        mapOf(
            "Mon" to false,
            "Tue" to false,
            "Wed" to false,
            "Thu" to false,
            "Fri" to false,
            "Sat" to false,
            "Sun" to false
        )
    ) }
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    LaunchedEffect(auth.currentUser) {
    }
    Log.d("HomeScreen", "isSkipped: $isSkipped")

    // Function to handle navigation
    // Function to handle navigation
    fun handleNavigation(route: String) {
        // Check login status first
        if (!isLoggedIn) {
            showDialog = true // Show dialog if not logged in
            return
        }

        // Check if skipped is true
        if (isSkipped) {
            showDialog = true // Show dialog when skipped is true
        } else {
            // Proceed with navigation based on phone number or user authentication
            when {
                phoneNumber != null && phoneNumber != "No phone number" -> {
                    navController.navigate("phone_number_profile_screen")
                }
                FirebaseAuth.getInstance().currentUser != null -> {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    navController.navigate("google_profile_screen/$userId") {
                        popUpTo("login") { inclusive = true }
                    }
                }
                else -> {
                    navController.navigate(route)
                }
            }
        }
    }

    // Dialog for prompting login
    if (showDialog) {

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Login Required") },
            text = { Text("You need to log in to access this feature.") },
            confirmButton = {
                TextButton(onClick = {
                    navController.navigate("your_goals")
                    viewModel.setSkipLoginState(false)
                    showDialog = false
                }) {
                    Text("Log In")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .width(200.dp) // Adjust width as needed
                    .height(400.dp) // Adjust height as needed
                    .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                    .background(Color(0xFFFFFFF7)) // Background color of drawer
            ) {
                ModalDrawerSheet {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // First button
                        TextButton(onClick = {
                            if (isLoggedIn) {
                                handleNavigation("profile")
                            } else {
                                showDialog = true // Show dialog if not logged in
                            }
                            scope.launch { drawerState.close() }
                        }) {
                            Text("Profile")
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // Second button
                        TextButton(onClick = {
                            if (isSkipped) {
                                showInterstitialAd()
                                showDialog = true
                            } else {
                                navController.navigate("about_us")
                            }
                        }) {
                            Text("About Us")
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        TextButton(onClick = {
                            if (isSkipped) {
                                showInterstitialAd()
                                showDialog = true
                            } else {
                                navController.navigate("fitness_assistant")
                            }
                            scope.launch { drawerState.close() }
                        }) {
                            Text("Fitness Assistant")
                        }

                    }
                }
            }
        }
    ) {
        Scaffold(
            containerColor = Color(0xFF011C2D),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            showInterstitialAd()
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (isSkipped) {
                                showInterstitialAd()
                                showDialog = true
                            } else {
                                navController.navigate("fitness_assistant")
                            }
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.ai_icon),
                                contentDescription = "fitness_assistant",
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF025D93),
                        titleContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                Column( modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)) {
                    BannerAdView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        adUnitId = "ca-app-pub-3940256099942544/6300978111" // Replace with your actual Ad Unit ID
                    )
                    BottomNavigationBar(selectedItem, onItemSelected)
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF025D93),
                                Color(0xFF024974),
                                Color(0xFF011C2D)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    // Card with Target and Weekly Schedule
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF025D93),
                                    Color(0xFF024974),
                                    Color(0xFF011C2D)
                                )
                            )
                        )){
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            // Target Text
                            Text(
                                text = "Target : $targetArea",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontFamily = FontFamily(Font(R.font.poppins_regular))
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Days of the week
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                DaysOfWeek(
                                    day = "Mon",
                                    isActive = currentDay == "Mon",
                                    hasAccessed = accessTracker.value["Mon"] ?: false
                                )
                                DaysOfWeek(
                                    day = "Tue",
                                    isActive = currentDay == "Tue",
                                    hasAccessed = accessTracker.value["Tue"] ?: false
                                )
                                DaysOfWeek(
                                    day = "Wed",
                                    isActive = currentDay == "Wed",
                                    hasAccessed = accessTracker.value["Wed"] ?: false
                                )
                                DaysOfWeek(
                                    day = "Thu",
                                    isActive = currentDay == "Thu",
                                    hasAccessed = accessTracker.value["Thu"] ?: false
                                )
                                DaysOfWeek(
                                    day = " Fri ",
                                    isActive = currentDay == "Fri",
                                    hasAccessed = accessTracker.value["Fri"] ?: false
                                )
                                DaysOfWeek(
                                    day = "Sat",
                                    isActive = currentDay == "Sat",
                                    hasAccessed = accessTracker.value["Sat"] ?: false
                                )
                                DaysOfWeek(
                                    day = "Sun",
                                    isActive = currentDay == "Sun",
                                    hasAccessed = accessTracker.value["Sun"] ?: false
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Small rounded image with text
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.home_icon), // Replace with your image resource
                                    contentDescription = "Best Time Image",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "Best time to get in shape",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF025D93),
                                        Color(0xFF024974),
                                        Color(0xFF011C2D)
                                    )
                                )
                            )
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp, top = 8.dp)
                                .padding(4.dp), // Padding to ensure the card is not clipped
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Make card background transparent
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {
                                // Row with Icon and Text
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = goal,
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily(Font(R.font.roboto_bold)),
                                        modifier = Modifier.align(Alignment.CenterVertically),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Icon(
                                        painter = painterResource(id = R.drawable.muscle_icon), // Replace with your icon
                                        contentDescription = "Icon",
                                        modifier = Modifier.size(32.dp),
                                        tint = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Nested Card-like Shape with Text
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Color(0xFFE0E0E0),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(16.dp)
                                        .padding(start = 24.dp, end = 24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column {
                                        Text(
                                            text = "12",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF025D93),
                                            fontSize = 24.sp,
                                            modifier = Modifier.align(Alignment.CenterHorizontally),
                                            textAlign = TextAlign.Center,
                                            fontFamily = FontFamily(Font(R.font.poppins_bold))
                                        )
                                        Text(
                                            text = "Weeks",
                                            fontFamily = FontFamily(Font(R.font.poppins_medium)),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 18.sp,
                                            modifier = Modifier.align(Alignment.CenterHorizontally),
                                            color = Color(0xFF025D93)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // New Card with Image and Text
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White, RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF025D93),
                                        Color(0xFF024974),
                                        Color(0xFF011C2D)
                                    )
                                )
                            )
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isSkipped) {
                                        showDialog = true
                                    } else {
                                        showInterstitialAd()
                                        showDialog2= true
                                    }
                                }
                                .padding(4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Rounded Image
                                Image(
                                    painter = painterResource(id = R.drawable.consultation_card_img), // Replace with your image resource
                                    contentDescription = "Rounded Image",
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                // Column for Texts
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Consult with Top ",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Health Experts!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Get personalized advice from",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "certified doctors, just a click away!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }

                                // Icon at the end
                                Icon(
                                    painter = painterResource(id = R.drawable.go_icon), // Replace with your icon resource
                                    contentDescription = "Icon",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { showDialog2 = true },
                                    tint = Color.White
                                )
                            }
                        }
                    }
                    if (showDialog2) {
                        Dialog(onDismissRequest = { showDialog2 = false }) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {
                                Box(modifier = Modifier .background(
                                    Color.White
                                )) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        // First Card with Image and Text
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    showDialog2 = false
                                                    navController.navigate("consultant_screen") }
                                                .padding(8.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.doc_img), // Replace with your image resource
                                                    contentDescription = "Image 1",
                                                    modifier = Modifier.size(60.dp)
                                                )
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Column {
                                                    Text(
                                                        text = "Dr. Sana Mumtaz",
                                                        color = Color.Black,
                                                        fontFamily = FontFamily(Font(R.font.roboto_bold)),
                                                        fontSize = 14.sp
                                                    )
                                                    Text(
                                                        text = "Licensed Physiotherapist",
                                                        color = Color.DarkGray,
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            }
                                        }
                                        Divider(color = Color.Gray, thickness = 1.dp) // Horizontal Divider
                                        // Second Card with Image and Text
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    showDialog2 = false
                                                    navController.navigate("consultant_screen2")
                                                }
                                                .padding(8.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.doc_profile_img_2), // Replace with your image resource
                                                    contentDescription = "Image 2",
                                                    modifier = Modifier.size(60.dp)
                                                )
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Column {
                                                    Text(
                                                        text = "Dr. Ayesha Irshad",
                                                        color = Color.Black,
                                                        fontSize = 14.sp,
                                                        fontFamily = FontFamily(Font(R.font.roboto_bold))
                                                    )
                                                    Text(
                                                        text = "Nutritionist",
                                                        color = Color.DarkGray,
                                                        fontSize = 10.sp,
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Close Button
                                        Button(
                                            onClick = { showDialog2 = false },
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        ) {
                                            Text(text = "Close",
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily(Font(R.font.roboto_regular))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // State to track the currently playing video
                    var playingVideoResId by remember { mutableStateOf<Int?>(null) }
                    Spacer(modifier = Modifier.height(8.dp))
                    val imageResIds = listOf(
                        R.drawable.image1,
                        R.drawable.image2,
                        R.drawable.image3,
                        R.drawable.image4
                    )
                    val scrollState = rememberScrollState()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val rows = imageResIds.chunked(2) // Divide the list into chunks of 2 (2 columns)

                            rows.forEach { rowImages ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowImages.forEach { imageResId ->
                                        Image(
                                            painter = painterResource(id = imageResId),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .weight(1f) // Divide space equally
                                                .aspectRatio(1f) // Maintain square shape
                                                .clip(RoundedCornerShape(12.dp)) // Rounded corners
                                                .background(Color.Gray) // Optional: Add background color
                                        )
                                    }
                                    if (rowImages.size < 2) {
                                        Spacer(modifier = Modifier.weight(1f)) // Add empty space if there are less than 2 items
                                    }
                                }
                            }
                        }

                }
                }
            }
        }
    }
}

data class VideoItem(val videoUrl: String, val title: String )

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(videoResId: Int, modifier: Modifier = Modifier, isPlaying: Boolean) {
    val context = LocalContext.current

    // Create and remember the ExoPlayer instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            // Set the media item to play from resources
            val mediaItem = MediaItem.fromUri(
                Uri.parse("android.resource://${context.packageName}/$videoResId")
            )
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = isPlaying
        }
    }

    // Update playback state when isPlaying changes
    LaunchedEffect(isPlaying) {
        exoPlayer.playWhenReady = isPlaying
    }

    // Dispose of ExoPlayer when the composable leaves the composition
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Create a PlayerView and assign ExoPlayer to it
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun VideoCard(videoResId: Int, isPlaying: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(16f / 9f)
            .padding(16.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Transparent background
    ) {
        // Video Player
        VideoPlayer(videoResId = videoResId, modifier = Modifier.fillMaxSize(), isPlaying = isPlaying)
    }
}



@Composable
fun DaysOfWeek(day: String, isActive: Boolean, hasAccessed: Boolean) {
    val color = when {
        isActive -> Color(0xFF025D93)
        hasAccessed -> Color(0xFFC8E6C9) // Light green for days accessed
        else -> Color.White
    }

    // Text color logic
    val textColor = if (isActive) Color.White else Color.Black

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .clip(CircleShape)
            .background(color)
            .padding(4.dp)
    ) {
        Text(
            text = day,
            color = textColor,
            fontSize = 10.sp,
            fontFamily = FontFamily(Font(R.font.poppins_medium)),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

fun getCurrentDay(): String {
    val calendar = Calendar.getInstance()
    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "Mon"
        Calendar.TUESDAY -> "Tue"
        Calendar.WEDNESDAY -> "Wed"
        Calendar.THURSDAY -> "Thu"
        Calendar.FRIDAY -> "Fri"
        Calendar.SATURDAY -> "Sat"
        Calendar.SUNDAY -> "Sun"
        else -> "Unknown"
    }
}

@Composable
fun BottomNavigationBar(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(
        modifier = Modifier
            .wrapContentHeight()
            .clip(RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp))
            .background(Color(0xFFD9D9D9)),
        containerColor = Color.Transparent,
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        val iconSize = 40.dp
        val itemPadding = 0.dp
        val iconPadding = 2.dp


        // Home Icon
        NavigationBarItem(
            icon = {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .padding(iconPadding)
                        .background(
                            color = if (selectedItem == 0) Color(0xFF025D93) else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Home",
                        modifier = Modifier.size(24.dp),
                        tint = if (selectedItem == 0) Color.White else Color.DarkGray
                    )
                }
            },
            modifier = Modifier.padding(itemPadding),
            label = {
                Text(
                    text = "Home",
                    fontSize = 10.sp,
                    color = if (selectedItem == 0) Color(0xFF025D93) else Color.DarkGray
                )
            },
            selected = selectedItem == 0,
            onClick = { onItemSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Transparent,
                unselectedIconColor = Color.DarkGray,
                indicatorColor = Color.Transparent
            )
        )

        // Workout Icon
        NavigationBarItem(
            icon = {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .padding(iconPadding)
                        .background(
                            color = if (selectedItem == 1) Color(0xFF025D93) else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.workout_icon),
                        contentDescription = "Workout",
                        modifier = Modifier.size(24.dp),
                        tint = if (selectedItem == 1) Color.White else Color.DarkGray
                    )
                }
            },
            modifier = Modifier.padding(itemPadding),
            label = {
                Text(
                    text = "Workout",
                    fontSize = 10.sp,
                    color = if (selectedItem == 1) Color(0xFF025D93) else Color.DarkGray
                )
            },
            selected = selectedItem == 1,
            onClick = { onItemSelected(1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Transparent,
                unselectedIconColor = Color.DarkGray,
                indicatorColor = Color.Transparent
            )
        )

        // Plans Icon
        NavigationBarItem(
            icon = {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .padding(iconPadding)
                        .background(
                            color = if (selectedItem == 2) Color(0xFF025D93) else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.plan_icon),
                        contentDescription = "Plans",
                        modifier = Modifier.size(24.dp),
                        tint = if (selectedItem == 2) Color.White else Color.DarkGray
                    )
                }
            },
            modifier = Modifier.padding(itemPadding),
            label = {
                Text(
                    text = "Plans",
                    fontSize = 10.sp,
                    color = if (selectedItem == 2) Color(0xFF025D93) else Color.DarkGray
                )
            },
            selected = selectedItem == 2,
            onClick = { onItemSelected(2) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Transparent,
                unselectedIconColor = Color.DarkGray,
                indicatorColor = Color.Transparent
            )
        )

        // Settings Icon
        NavigationBarItem(
            icon = {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .padding(iconPadding)
                        .background(
                            color = if (selectedItem == 3) Color(0xFF025D93) else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(24.dp),
                        tint = if (selectedItem == 3) Color.White else Color.DarkGray
                    )
                }
            },
            label = {
                Text(
                    text = "Settings",
                    fontSize = 10.sp,
                    color = if (selectedItem == 3) Color(0xFF025D93) else Color.DarkGray
                )
            },
            modifier = Modifier.padding(itemPadding),
            selected = selectedItem == 3,
            onClick = { onItemSelected(3) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Transparent,
                unselectedIconColor = Color.DarkGray,
                indicatorColor = Color.Transparent
            )
        )
    }
}







