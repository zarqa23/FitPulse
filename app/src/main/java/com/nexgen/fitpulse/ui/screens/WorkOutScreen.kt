package com.nexgen.fitpulse.ui.screens

import android.content.Context
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.airbnb.lottie.model.Font
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nexgen.fitpulse.R
import com.nexgen.fitpulse.ui.components.BannerAdView
import com.nexgen.fitpulse.ui.viewmodel.UserSelectionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(selectedItem: Int, onItemSelected: (Int) -> Unit, onBackClick: () -> Unit, viewModel: UserSelectionViewModel , navController: NavController) {
    val selectedExperience = viewModel.selectedExperience.value
    // Determine the title based on selected experience
    val planTitle = when (selectedExperience) {
        "Beginner" -> "Beginner Workout Plan"
        "Intermediate" -> "Intermediate Workout Plan"
        "Advanced" -> "Advanced Workout Plan"
        else -> "Diet Plan" // Default title if no experience selected
    }

    Scaffold(
        containerColor = Color(0xFF011C2D),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = planTitle,
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF025D93),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Column {
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
            if (selectedExperience == null) {
                // Show the list of plans if no specific plan is selected
                Column {
                    PlanBox(title = "Beginner 7-Day Plan") {
                        viewModel.selectedExperience
                    }
                    PlanBox(title = "Intermediate 7-Day Plan") {
                        viewModel.selectedExperience
                    }
                    PlanBox(title = "Advanced 7-Day Plan") {
                        viewModel.selectedExperience
                    }
                }
            } else {
                // Show the selected plan details
                when (selectedExperience) {
                    "Beginner" -> BeginnerPlan(navController)
                    "Intermediate" -> IntermediatePlan(navController)
                    "Advanced" -> AdvancedPlan(navController)
                }
            }
        }
    }
}


@Composable
fun PlanDayCard(
    day: String,
    imageRes: Int,
    title: String,
    description: String,
    extraInfo: String,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isUnlocked) { if (isUnlocked) onClick() }
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color.White else Color.LightGray,
            contentColor = Color(0xFF025D93)
        )
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Plan Image",
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isUnlocked) Color(0xFF025D93) else Color(0xFF025D93)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUnlocked) Color(0xFF025D93) else Color(0xFF025D93)
                )
                Text(
                    text = extraInfo,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUnlocked) Color(0xFF025D93) else Color(0xFF025D93)
                )
            }
        }
    }
}

@Composable
fun BeginnerPlan(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Keys for storing button states and timestamps
    var buttonUnlockTimestamps by remember { mutableStateOf<Map<Int, Long>>(emptyMap()) }
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Load button unlock timestamps from DataStore
    LaunchedEffect(Unit) {
        val preferences = context.dataStore.data.first()
        buttonUnlockTimestamps = (1..7).associateWith { day ->
            preferences[longPreferencesKey("beg_day${day}_unlock_time")] ?: (System.currentTimeMillis() + TimeUnit.HOURS.toMillis(
                (24 * (day - 1)).toLong()
            ))
        }
    }

    // Update current time periodically to check button states
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000) // Update every second
        }
    }

    // Helper function to check if the card is unlocked
    fun isCardUnlocked(day: Int): Boolean {
        return currentTime >= (buttonUnlockTimestamps[day] ?: Long.MAX_VALUE)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
        PlanDayCard(
            day = "Day 1",
            imageRes = R.drawable.workouts_icon,  // Replace with actual image resource
            title = "Day 1",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = true, // Always unlocked
            onClick = {
                navController.navigate("beg_day1_workout")
            }
        )

        // Day 2 Card
        PlanDayCard(
            day = "Day 2",
            imageRes = R.drawable.workouts_icon,
            title = "Day 2",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(2),
            onClick = {
                navController.navigate("beg_day2_workout")
            }
        )

        // Day 3 Card
        PlanDayCard(
            day = "Day 3",
            imageRes = R.drawable.workouts_icon,
            title = "Day 3",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(3),
            onClick = {
                navController.navigate("beg_day3_workout")
            }
        )

        // Day 4 Card
        PlanDayCard(
            day = "Day 4",
            imageRes = R.drawable.workouts_icon,
            title = "Day 4",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(4),
            onClick = {
                navController.navigate("beg_day4_workout")
            }
        )

        // Day 5 Card
        PlanDayCard(
            day = "Day 5",
            imageRes = R.drawable.workouts_icon,
            title = "Day 5",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(5),
            onClick = {
                navController.navigate("beg_day5_workout")
            }
        )

        // Day 6 Card
        PlanDayCard(
            day = "Day 6",
            imageRes = R.drawable.workouts_icon,
            title = "Day 6",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(6),
            onClick = {
                navController.navigate("beg_day6_workout")
            }
        )

        // Day 7 Card
        PlanDayCard(
            day = "Day 7",
            imageRes = R.drawable.workouts_icon,
            title = "Day 7",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(7),
            onClick = {
                navController.navigate("beg_day7_workout")
            }
        )
    }
}


@Composable
fun IntermediatePlan(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Keys for storing button states and timestamps
    var buttonUnlockTimestamps by remember { mutableStateOf<Map<Int, Long>>(emptyMap()) }
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Load button unlock timestamps from DataStore
    LaunchedEffect(Unit) {
        val preferences = context.dataStore.data.first()
        buttonUnlockTimestamps = (1..7).associateWith { day ->
            preferences[longPreferencesKey("beg_day${day}_unlock_time")] ?: (System.currentTimeMillis() + TimeUnit.HOURS.toMillis(
                (24 * (day - 1)).toLong()
            ))
        }
    }

    // Update current time periodically to check button states
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000) // Update every second
        }
    }

    // Helper function to check if the card is unlocked
    fun isCardUnlocked(day: Int): Boolean {
        return currentTime >= (buttonUnlockTimestamps[day] ?: Long.MAX_VALUE)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
        PlanDayCard(
            day = "Day 1",
            imageRes = R.drawable.workouts_icon,  // Replace with actual image resource
            title = "Day 1",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = true, // Always unlocked
            onClick = {
                navController.navigate("int_day1_workout")
            }
        )

        // Day 2 Card
        PlanDayCard(
            day = "Day 2",
            imageRes = R.drawable.workouts_icon,
            title = "Day 2",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(2),
            onClick = {
                navController.navigate("int_day2_workout")
            }
        )

        // Day 3 Card
        PlanDayCard(
            day = "Day 3",
            imageRes = R.drawable.workouts_icon,
            title = "Day 3",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(3),
            onClick = {
                navController.navigate("int_day3_workout")
            }
        )

        // Day 4 Card
        PlanDayCard(
            day = "Day 4",
            imageRes = R.drawable.workouts_icon,
            title = "Day 4",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(4),
            onClick = {
                navController.navigate("int_day4_workout")
            }
        )

        // Day 5 Card
        PlanDayCard(
            day = "Day 5",
            imageRes = R.drawable.workouts_icon,
            title = "Day 5",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(5),
            onClick = {
                navController.navigate("int_day5_workout")
            }
        )

        // Day 6 Card
        PlanDayCard(
            day = "Day 6",
            imageRes = R.drawable.workouts_icon,
            title = "Day 6",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(6),
            onClick = {
                navController.navigate("int_day6_workout")
            }
        )

        // Day 7 Card
        PlanDayCard(
            day = "Day 7",
            imageRes = R.drawable.workouts_icon,
            title = "Day 7",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(7),
            onClick = {
                navController.navigate("int_day7_workout")
            }
        )
    }
}


@Composable
fun AdvancedPlan(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Keys for storing button states and timestamps
    var buttonUnlockTimestamps by remember { mutableStateOf<Map<Int, Long>>(emptyMap()) }
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Load button unlock timestamps from DataStore
    LaunchedEffect(Unit) {
        val preferences = context.dataStore.data.first()
        buttonUnlockTimestamps = (1..7).associateWith { day ->
            preferences[longPreferencesKey("beg_day${day}_unlock_time")] ?: (System.currentTimeMillis() + TimeUnit.HOURS.toMillis(
                (24 * (day - 1)).toLong()
            ))
        }
    }

    // Update current time periodically to check button states
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000) // Update every second
        }
    }

    // Helper function to check if the card is unlocked
    fun isCardUnlocked(day: Int): Boolean {
        return currentTime >= (buttonUnlockTimestamps[day] ?: Long.MAX_VALUE)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
        PlanDayCard(
            day = "Day 1",
            imageRes = R.drawable.workouts_icon,  // Replace with actual image resource
            title = "Day 1",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = true, // Always unlocked
            onClick = {
                navController.navigate("adv_day1_workout")
            }
        )

        // Day 2 Card
        PlanDayCard(
            day = "Day 2",
            imageRes = R.drawable.workouts_icon,
            title = "Day 2",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(2),
            onClick = {
                navController.navigate("adv_day2_workout")
            }
        )

        // Day 3 Card
        PlanDayCard(
            day = "Day 3",
            imageRes = R.drawable.workouts_icon,
            title = "Day 3",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(3),
            onClick = {
                navController.navigate("adv_day3_workout")
            }
        )

        // Day 4 Card
        PlanDayCard(
            day = "Day 4",
            imageRes = R.drawable.workouts_icon,
            title = "Day 4",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(4),
            onClick = {
                navController.navigate("adv_day4_workout")
            }
        )

        // Day 5 Card
        PlanDayCard(
            day = "Day 5",
            imageRes = R.drawable.workouts_icon,
            title = "Day 5",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(5),
            onClick = {
                navController.navigate("adv_day5_workout")
            }
        )

        // Day 6 Card
        PlanDayCard(
            day = "Day 6",
            imageRes = R.drawable.workouts_icon,
            title = "Day 6",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(6),
            onClick = {
                navController.navigate("adv_day6_workout")
            }
        )

        // Day 7 Card
        PlanDayCard(
            day = "Day 7",
            imageRes = R.drawable.workouts_icon,
            title = "Day 7",
            description = "7 Exercises",
            extraInfo = "Duration: 5 mins",
            isUnlocked = isCardUnlocked(7),
            onClick = {
                navController.navigate("adv_day7_workout")
            }
        )
    }
}


@Composable
fun PlanBox(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginnerDay1WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beginner Day 1 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // List of exercises
            ExerciseCard("Incline bench row", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.incline_bench_row_excersise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("beg_detailed_day_1")})
            ExerciseCard("Modified knee push - ups", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.modified_knee_push_ups , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_2")})
            ExerciseCard("Plank shoulder tap", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plank_sholder_tap , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_3")})
            ExerciseCard("Parallel dip bar", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.parallel_dip_bar , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_4")})
            ExerciseCard("PLANK Roll", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plankroll , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_5")})
            ExerciseCard("Rope pull down", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.rope_pull_down , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_6")})
            ExerciseCard("Single arm bent over", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.single_arm_bent_over , modifier = Modifier.size(50.dp) ) },onClick = {navController.navigate("beg_detailed_day_7")})


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginnerDay2WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beginner Day 2 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Day 2
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Day 2
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of exercises
            ExerciseCard("Incline bench row", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.incline_bench_row_excersise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("beg_detailed_day_1")})
            ExerciseCard("Modified knee push - ups", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.modified_knee_push_ups , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_2")})
            ExerciseCard("Plank shoulder tap", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plank_sholder_tap , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_3")})
            ExerciseCard("Parallel dip bar", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.parallel_dip_bar , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_4")})
            ExerciseCard("PLANK Roll", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plankroll , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_5")})
            ExerciseCard("Rope pull down", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.rope_pull_down , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_6")})
            ExerciseCard("Single arm bent over", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.single_arm_bent_over , modifier = Modifier.size(50.dp) ) },onClick = {navController.navigate("beg_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginnerDay3WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beginner Day 3 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Day 3
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Day 3
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of exercises
            ExerciseCard("Incline bench row", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.incline_bench_row_excersise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("beg_detailed_day_1")})
            ExerciseCard("Modified knee push - ups", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.modified_knee_push_ups , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_2")})
            ExerciseCard("Plank shoulder tap", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plank_sholder_tap , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_3")})
            ExerciseCard("Parallel dip bar", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.parallel_dip_bar , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_4")})
            ExerciseCard("PLANK Roll", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plankroll , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_5")})
            ExerciseCard("Rope pull down", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.rope_pull_down , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_6")})
            ExerciseCard("Single arm bent over", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.single_arm_bent_over , modifier = Modifier.size(50.dp) ) },onClick = {navController.navigate("beg_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginnerDay4WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beginner Day 4 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Day 4
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Day 4
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of exercises
            ExerciseCard("Incline bench row", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.incline_bench_row_excersise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("beg_detailed_day_1")})
            ExerciseCard("Modified knee push - ups", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.modified_knee_push_ups , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_2")})
            ExerciseCard("Plank shoulder tap", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plank_sholder_tap , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_3")})
            ExerciseCard("Parallel dip bar", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.parallel_dip_bar , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_4")})
            ExerciseCard("PLANK Roll", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plankroll , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_5")})
            ExerciseCard("Rope pull down", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.rope_pull_down , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_6")})
            ExerciseCard("Single arm bent over", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.single_arm_bent_over , modifier = Modifier.size(50.dp) ) },onClick = {navController.navigate("beg_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginnerDay5WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beginner Day 5 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Day 5
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Day 5
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of exercises
            ExerciseCard("Incline bench row", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.incline_bench_row_excersise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("beg_detailed_day_1")})
            ExerciseCard("Modified knee push - ups", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.modified_knee_push_ups , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_2")})
            ExerciseCard("Plank shoulder tap", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plank_sholder_tap , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_3")})
            ExerciseCard("Parallel dip bar", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.parallel_dip_bar , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_4")})
            ExerciseCard("PLANK Roll", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plankroll , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_5")})
            ExerciseCard("Rope pull down", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.rope_pull_down , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_6")})
            ExerciseCard("Single arm bent over", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.single_arm_bent_over , modifier = Modifier.size(50.dp) ) },onClick = {navController.navigate("beg_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginnerDay6WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beginner Day 6 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Day 6
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Day 6
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // List of exercises
            ExerciseCard("Incline bench row", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.incline_bench_row_excersise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("beg_detailed_day_1")})
            ExerciseCard("Modified knee push - ups", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.modified_knee_push_ups , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_2")})
            ExerciseCard("Plank shoulder tap", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plank_sholder_tap , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_3")})
            ExerciseCard("Parallel dip bar", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.parallel_dip_bar , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_4")})
            ExerciseCard("PLANK Roll", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plankroll , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_5")})
            ExerciseCard("Rope pull down", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.rope_pull_down , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_6")})
            ExerciseCard("Single arm bent over", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.single_arm_bent_over , modifier = Modifier.size(50.dp) ) },onClick = {navController.navigate("beg_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginnerDay7WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beginner Day 7 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Day 7
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Day 7
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of exercises
            ExerciseCard("Incline bench row", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.incline_bench_row_excersise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("beg_detailed_day_1")})
            ExerciseCard("Modified knee push - ups", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.modified_knee_push_ups , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_2")})
            ExerciseCard("Plank shoulder tap", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plank_sholder_tap , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_3")})
            ExerciseCard("Parallel dip bar", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.parallel_dip_bar , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_4")})
            ExerciseCard("PLANK Roll", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plankroll , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_5")})
            ExerciseCard("Rope pull down", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.rope_pull_down , modifier = Modifier.size(50.dp) ) }, onClick = {navController.navigate("beg_detailed_day_6")})
            ExerciseCard("Single arm bent over", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.single_arm_bent_over , modifier = Modifier.size(50.dp) ) },onClick = {navController.navigate("beg_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntermediateDay1WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Intermediate Day 1 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Intermediate Day 1
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Intermediate Day 1
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // List of exercises
            ExerciseCard("Sit twist", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.sit_twist_exercise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_1")})
            ExerciseCard("Sit ups", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.situps , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_2")})
            ExerciseCard("Twist", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.twist , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_3")})
            ExerciseCard("Parallel dip", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.parallel_dip_bar , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_4")})
            ExerciseCard("PLANK Roll", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plankroll , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_5")})
            ExerciseCard("Single arm bent over", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.single_arm_bent_over , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_6")})
            ExerciseCard("Lunges", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.lunges , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntermediateDay2WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Intermediate Day 2 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Intermediate Day 2
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Intermediate Day 2
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // List of exercises
            ExerciseCard("Sit twist", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.sit_twist_exercise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_1")})
            ExerciseCard("Sit ups", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.situps , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_2")})
            ExerciseCard("Twist", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.twist , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_3")})
            ExerciseCard("Parallel dip", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.parallel_dip_bar , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_4")})
            ExerciseCard("PLANK Roll", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plankroll , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_5")})
            ExerciseCard("Single arm bent over", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.single_arm_bent_over , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_6")})
            ExerciseCard("Lunges", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.lunges , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_7")})


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntermediateDay3WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Intermediate Day 3 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Intermediate Day 3
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Intermediate Day 3
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // List of exercises
            ExerciseCard("Sit twist", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.sit_twist_exercise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_1")})
            ExerciseCard("Sit ups", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.situps , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_2")})
            ExerciseCard("Twist", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.twist , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_3")})
            ExerciseCard("Parallel dip", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.parallel_dip_bar , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_4")})
            ExerciseCard("PLANK Roll", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plankroll , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_5")})
            ExerciseCard("Single arm bent over", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.single_arm_bent_over , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_6")})
            ExerciseCard("Lunges", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.lunges , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntermediateDay4WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Intermediate Day 4 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Intermediate Day 4
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Intermediate Day 4
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // List of exercises
            ExerciseCard("Sit twist", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.sit_twist_exercise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_1")})
            ExerciseCard("Sit ups", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.situps , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_2")})
            ExerciseCard("Twist", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.twist , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_3")})
            ExerciseCard("Parallel dip", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.parallel_dip_bar , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_4")})
            ExerciseCard("PLANK Roll", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plankroll , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_5")})
            ExerciseCard("Single arm bent over", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.single_arm_bent_over , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_6")})
            ExerciseCard("Lunges", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.lunges , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntermediateDay5WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Intermediate Day 5 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Intermediate Day 5
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Intermediate Day 5
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of exercises
            ExerciseCard("Sit twist", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.sit_twist_exercise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_1")})
            ExerciseCard("Sit ups", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.situps , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_2")})
            ExerciseCard("Twist", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.twist , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_3")})
            ExerciseCard("Parallel dip", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.parallel_dip_bar , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_4")})
            ExerciseCard("PLANK Roll", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plankroll , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_5")})
            ExerciseCard("Single arm bent over", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.single_arm_bent_over , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_6")})
            ExerciseCard("Lunges", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.lunges , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_7")})

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntermediateDay6WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Intermediate Day 6 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Intermediate Day 6
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Intermediate Day 6
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of exercises
            ExerciseCard("Sit twist", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.sit_twist_exercise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_1")})
            ExerciseCard("Sit ups", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.situps , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_2")})
            ExerciseCard("Twist", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.twist , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_3")})
            ExerciseCard("Parallel dip", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.parallel_dip_bar , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_4")})
            ExerciseCard("PLANK Roll", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plankroll , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_5")})
            ExerciseCard("Single arm bent over", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.single_arm_bent_over , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_6")})
            ExerciseCard("Lunges", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.lunges , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntermediateDay7WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Intermediate Day 7 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Intermediate Day 7
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Intermediate Day 7
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // List of exercises
            ExerciseCard("Sit twist", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.sit_twist_exercise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_1")})
            ExerciseCard("Sit ups", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.situps , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_2")})
            ExerciseCard("Twist", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.twist , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_3")})
            ExerciseCard("Parallel dip", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.parallel_dip_bar , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_4")})
            ExerciseCard("PLANK Roll", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.plankroll , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_5")})
            ExerciseCard("Single arm bent over", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.single_arm_bent_over , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_6")})
            ExerciseCard("Lunges", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.lunges , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("int_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedDay1WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advanced Day 1 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Advanced Day 1
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Advanced Day 1
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of exercises
            ExerciseCard("Hanging leg raises", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_leg_raises , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_1")})
            ExerciseCard("Incline bench row","00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.incline_bench_row_excersise , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_2")})
            ExerciseCard("Dips on parallel bars", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dips_on_parallel_bar , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_3")})
            ExerciseCard("Dumbbell flat bench press", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dumbbell_flat_bench_press , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_4")})
            ExerciseCard("Dumb bell weight leg pull", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dumbbell_weight_leggpull , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_5")})
            ExerciseCard("Hanging knee raise", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_knee_raise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_6")})
            ExerciseCard("Hanging knee front knee lift", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_leg_raises , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedDay2WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advanced Day 2 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Advanced Day 2
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Advanced Day 2
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of exercises
            ExerciseCard("Hanging leg raises", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_leg_raises , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_1")})
            ExerciseCard("Incline bench row","00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.incline_bench_row_excersise , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_2")})
            ExerciseCard("Dips on parallel bars", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dips_on_parallel_bar , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_3")})
            ExerciseCard("Dumbbell flat bench press", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dumbbell_flat_bench_press , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_4")})
            ExerciseCard("Dumb bell weight leg pull", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dumbbell_weight_leggpull , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_5")})
            ExerciseCard("Hanging knee raise", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_knee_raise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_6")})
            ExerciseCard("Hanging knee front knee lift", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_leg_raises , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedDay3WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advanced Day 3 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Advanced Day 3
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Advanced Day 3
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of exercises
            ExerciseCard("Hanging leg raises", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_leg_raises , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_1")})
            ExerciseCard("Incline bench row","00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.incline_bench_row_excersise , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_2")})
            ExerciseCard("Dips on parallel bars", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dips_on_parallel_bar , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_3")})
            ExerciseCard("Dumbbell flat bench press", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dumbbell_flat_bench_press , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_4")})
            ExerciseCard("Dumb bell weight leg pull", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dumbbell_weight_leggpull , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_5")})
            ExerciseCard("Hanging knee raise", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_knee_raise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_6")})
            ExerciseCard("Hanging knee front knee lift", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_leg_raises , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedDay4WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advanced Day 4 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Advanced Day 4
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Advanced Day 4
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // List of exercises
            ExerciseCard("Hanging leg raises", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_leg_raises , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_1")})
            ExerciseCard("Incline bench row","00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.incline_bench_row_excersise , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_2")})
            ExerciseCard("Dips on parallel bars", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dips_on_parallel_bar , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_3")})
            ExerciseCard("Dumbbell flat bench press", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dumbbell_flat_bench_press , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_4")})
            ExerciseCard("Dumb bell weight leg pull", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dumbbell_weight_leggpull , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_5")})
            ExerciseCard("Hanging knee raise", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_knee_raise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_6")})
            ExerciseCard("Hanging knee front knee lift", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_leg_raises , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_7")})


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedDay5WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advanced Day 5 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Advanced Day 5
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Advanced Day 5
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of exercises
            ExerciseCard("Hanging leg raises", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_leg_raises , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_1")})
            ExerciseCard("Incline bench row","00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.incline_bench_row_excersise , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_2")})
            ExerciseCard("Dips on parallel bars", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dips_on_parallel_bar , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_3")})
            ExerciseCard("Dumbbell flat bench press", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dumbbell_flat_bench_press , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_4")})
            ExerciseCard("Dumb bell weight leg pull", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dumbbell_weight_leggpull , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_5")})
            ExerciseCard("Hanging knee raise", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_knee_raise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_6")})
            ExerciseCard("Hanging knee front knee lift", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_leg_raises , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedDay6WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advanced Day 6 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "40 Exercises",  // Update for Advanced Day 6
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "90 min",  // Update for Advanced Day 6
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of exercises
            ExerciseCard("Hanging leg raises", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_leg_raises , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_1")})
            ExerciseCard("Incline bench row","00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.incline_bench_row_excersise , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_2")})
            ExerciseCard("Dips on parallel bars", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dips_on_parallel_bar , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_3")})
            ExerciseCard("Dumbbell flat bench press", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dumbbell_flat_bench_press , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_4")})
            ExerciseCard("Dumb bell weight leg pull", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dumbbell_weight_leggpull , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_5")})
            ExerciseCard("Hanging knee raise", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_knee_raise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_6")})
            ExerciseCard("Hanging knee front knee lift", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_leg_raises , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedDay7WorkoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advanced Day 7 Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->  // paddingValues is passed here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)  // Apply padding values from Scaffold
                .verticalScroll(rememberScrollState())
                .padding(16.dp)  // Extra padding for content
        ) {
            // Main image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fitness_image),  // Replace with actual image resource
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay content (icons and text)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color(0xAA000000))  // Semi-transparent overlay
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with exercises count
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dumble_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7 Exercises",  // Update for Advanced Day 7
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Icon with duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_icon),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5 min",  // Update for Advanced Day 7
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // List of exercises
            ExerciseCard("Hanging leg raises", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_leg_raises , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_1")})
            ExerciseCard("Incline bench row","00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.incline_bench_row_excersise , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_2")})
            ExerciseCard("Dips on parallel bars", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dips_on_parallel_bar , modifier = Modifier.size(50.dp)) } , onClick = {navController.navigate("adv_detailed_day_3")})
            ExerciseCard("Dumbbell flat bench press", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dumbbell_flat_bench_press , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_4")})
            ExerciseCard("Dumb bell weight leg pull", "00:30",  imageContent = { GifImageFromDrawable(drawableResId = R.drawable.dumbbell_weight_leggpull , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_5")})
            ExerciseCard("Hanging knee raise", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_knee_raise , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_6")})
            ExerciseCard("Hanging knee front knee lift", "00:30", imageContent = { GifImageFromDrawable(drawableResId = R.drawable.hangging_leg_raises , modifier = Modifier.size(50.dp)) }, onClick = {navController.navigate("adv_detailed_day_7")})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginnerDay1DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.incline_bench_row_excersise)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black) // Changed to Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"An incline bench row involves leaning back on an incline bench, gripping a barbell or dumbbells with an underhand grip, and pulling them towards your chest, engaging your back muscles.\"",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black), // Changed to Black
                        fontStyle = FontStyle.Italic,

                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black) // Changed to Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentTime.isBefore(LocalTime.of(10, 0))) {
                        // Before 10 AM, only show breakfast
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 boiled egg\n1 slice of whole wheat bread\n1 cup low-fat yogurt with a spoonful of chia seeds"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mid-Morning Snack unlocks at 10:00 AM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
                        // Between 10 AM and 1 PM, show breakfast and morning snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 boiled egg\n1 slice of whole wheat bread\n1 cup low-fat yogurt with a spoonful of chia seeds"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 apple or orange"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lunch unlocks at 1:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
                        // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 boiled egg\n1 slice of whole wheat bread\n1 cup low-fat yogurt with a spoonful of chia seeds"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 apple or orange"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "1 chapati (whole wheat)\nGrilled chicken breast with a small side of mixed salad (tomato, cucumber, and lettuce)\n1 bowl of curd (dahi)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Evening Snack unlocks at 5:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
                        // Between 5 PM and 8 PM, show breakfast, snack, lunch, and evening snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 boiled egg\n1 slice of whole wheat bread\n1 cup low-fat yogurt with a spoonful of chia seeds"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 apple or orange"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "1 chapati (whole wheat)\nGrilled chicken breast with a small side of mixed salad (tomato, cucumber, and lettuce)\n1 bowl of curd (dahi)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Evening Snack",
                            content = "A handful of almonds or walnuts"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dinner unlocks at 8:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else {
                        // After 8 PM, show everything
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 boiled egg\n1 slice of whole wheat bread\n1 cup low-fat yogurt with a spoonful of chia seeds"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 apple or orange"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "1 chapati (whole wheat)\nGrilled chicken breast with a small side of mixed salad (tomato, cucumber, and lettuce)\n1 bowl of curd (dahi)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Evening Snack",
                            content = "A handful of almonds or walnuts"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Dinner",
                            content = "1 bowl of vegetable soup\n1 piece of grilled fish or tofu\nA small serving of steamed vegetables (broccoli, carrots, and peas)\n1 small serving of brown rice"
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginnerDay2DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.modified_knee_push_ups)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black) // Changed color to black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Modified knee push-ups involve performing push-ups with your knees bent and resting on the ground, making them easier than traditional push-ups.\"",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black), // Changed color to black
                        fontStyle = FontStyle.Italic,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black) // Changed color to black
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentTime.isBefore(LocalTime.of(10, 0))) {
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 bowl of oatmeal with honey and banana slices\n1 boiled egg"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mid-Morning Snack unlocks at 10:00 AM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 bowl of oatmeal with honey and banana slices\n1 boiled egg"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 pear or seasonal fruit"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lunch unlocks at 1:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 bowl of oatmeal with honey and banana slices\n1 boiled egg"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 pear or seasonal fruit"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "1 cup brown rice\nGrilled fish (e.g., rohu)\nMixed vegetable salad with olive oil dressing"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Evening Snack unlocks at 5:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 bowl of oatmeal with honey and banana slices\n1 boiled egg"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 pear or seasonal fruit"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "1 cup brown rice\nGrilled fish (e.g., rohu)\nMixed vegetable salad with olive oil dressing"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Evening Snack",
                            content = "1 cup green tea and 2 dates"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dinner unlocks at 8:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else {
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 bowl of oatmeal with honey and banana slices\n1 boiled egg"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 pear or seasonal fruit"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "1 cup brown rice\nGrilled fish (e.g., rohu)\nMixed vegetable salad with olive oil dressing"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Evening Snack",
                            content = "1 cup green tea and 2 dates"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Dinner",
                            content = "1 chapati\nChicken curry with minimal oil\nCucumber and yogurt raita"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginnerDay3DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.plank_sholder_tap)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"A plank shoulder tap involves starting in a plank position, then lifting one hand off the ground, tapping the opposite shoulder, and then returning to the starting position before repeating on the other side.\"", style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        fontStyle = FontStyle.Italic,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    when {
                        currentTime.isBefore(LocalTime.of(10, 0)) -> {
                            // Before 10 AM, only show breakfast
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "2 slices of whole wheat bread with peanut butter\n1 glass of fresh orange juice"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Mid-Morning Snack unlocks at 10:00 AM",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        currentTime.isBefore(LocalTime.of(13, 0)) -> {
                            // Between 10 AM and 1 PM, show breakfast and morning snack
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "2 slices of whole wheat bread with peanut butter\n1 glass of fresh orange juice"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 handful of roasted chickpeas (chana)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Lunch unlocks at 1:00 PM",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        currentTime.isBefore(LocalTime.of(17, 0)) -> {
                            // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "2 slices of whole wheat bread with peanut butter\n1 glass of fresh orange juice"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 handful of roasted chickpeas (chana)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Lunch",
                                content = "1 cup of quinoa or brown rice\nLentil curry (dal)\n1 bowl of salad (tomato, onion, and mint leaves)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Evening Snack unlocks at 5:00 PM",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        currentTime.isBefore(LocalTime.of(20, 0)) -> {
                            // Between 5 PM and 8 PM, show breakfast, snack, lunch, and evening snack
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "2 slices of whole wheat bread with peanut butter\n1 glass of fresh orange juice"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 handful of roasted chickpeas (chana)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Lunch",
                                content = "1 cup of quinoa or brown rice\nLentil curry (dal)\n1 bowl of salad (tomato, onion, and mint leaves)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Evening Snack",
                                content = "1 cup of yogurt with a sprinkle of flaxseeds"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Dinner unlocks at 8:00 PM",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        else -> {
                            // After 8 PM, show everything
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "2 slices of whole wheat bread with peanut butter\n1 glass of fresh orange juice"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 handful of roasted chickpeas (chana)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Lunch",
                                content = "1 cup of quinoa or brown rice\nLentil curry (dal)\n1 bowl of salad (tomato, onion, and mint leaves)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Evening Snack",
                                content = "1 cup of yogurt with a sprinkle of flaxseeds"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Dinner",
                                content = "Grilled chicken tikka with 1 chapati\nSteamed vegetables (e.g., cauliflower, peas, and carrots)"
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginnerDay4DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.parallel_dip_bar)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Workout Details Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Parallel dips involve hanging from parallel bars with your arms straight and then lowering your body until your elbows are at a 90-degree angle before pushing back up.\"",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        fontStyle = FontStyle.Italic,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Diet Plan Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Displaying each meal based on time
                    when {
                        currentTime.isBefore(LocalTime.of(10, 0)) -> {
                            // Before 10 AM
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "1 paratha made with olive oil and whole wheat flour\n1 boiled egg\nA glass of lassi (salted or sweetened with jaggery)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Mid-Morning Snack unlocks at 10:00 AM",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        currentTime.isBefore(LocalTime.of(13, 0)) -> {
                            // Between 10 AM and 1 PM
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "1 paratha made with olive oil and whole wheat flour\n1 boiled egg\nA glass of lassi (salted or sweetened with jaggery)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 guava or any seasonal fruit"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Lunch unlocks at 1:00 PM",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        currentTime.isBefore(LocalTime.of(17, 0)) -> {
                            // Between 1 PM and 5 PM
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "1 paratha made with olive oil and whole wheat flour\n1 boiled egg\nA glass of lassi (salted or sweetened with jaggery)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 guava or any seasonal fruit"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Lunch",
                                content = "1 bowl of vegetable pulao (using brown rice)\n1 bowl of raita with cucumber and cumin"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Evening Snack unlocks at 5:00 PM",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        currentTime.isBefore(LocalTime.of(20, 0)) -> {
                            // Between 5 PM and 8 PM
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "1 paratha made with olive oil and whole wheat flour\n1 boiled egg\nA glass of lassi (salted or sweetened with jaggery)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 guava or any seasonal fruit"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Lunch",
                                content = "1 bowl of vegetable pulao (using brown rice)\n1 bowl of raita with cucumber and cumin"
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Evening Snack",
                                content = "A handful of mixed nuts (almonds, walnuts, raisins)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Dinner unlocks at 8:00 PM",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        else -> {
                            // After 8 PM
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "1 paratha made with olive oil and whole wheat flour\n1 boiled egg\nA glass of lassi (salted or sweetened with jaggery)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 guava or any seasonal fruit"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Lunch",
                                content = "1 bowl of vegetable pulao (using brown rice)\n1 bowl of raita with cucumber and cumin"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Evening Snack",
                                content = "A handful of mixed nuts (almonds, walnuts, raisins)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Dinner",
                                content = "Grilled kebab (beef or chicken) with chutney\n1 chapati or naan\nCabbage salad"
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginnerDay5DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.plankroll)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Hold at the top, roll back into a forearm plank, and roll into your other side for another side plank\"",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        fontStyle = FontStyle.Italic,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    when {
                        currentTime.isBefore(LocalTime.of(10, 0)) -> {
                            // Before 10 AM, only show breakfast
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "1 bowl of porridge with milk and a drizzle of honey\n1 boiled egg"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Mid-Morning Snack unlocks at 10:00 AM",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        currentTime.isBefore(LocalTime.of(13, 0)) -> {
                            // Between 10 AM and 1 PM, show breakfast and morning snack
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "1 bowl of porridge with milk and a drizzle of honey\n1 boiled egg"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 cup of green tea with 2 whole wheat crackers"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Lunch unlocks at 1:00 PM",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        currentTime.isBefore(LocalTime.of(17, 0)) -> {
                            // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "1 bowl of porridge with milk and a drizzle of honey\n1 boiled egg"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 cup of green tea with 2 whole wheat crackers"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Lunch",
                                content = "1 chapati with daal (lentils)\n1 small bowl of kachumber salad (onion, tomato, cucumber, lemon)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Evening Snack unlocks at 5:00 PM",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        currentTime.isBefore(LocalTime.of(20, 0)) -> {
                            // Between 5 PM and 8 PM, show breakfast, snack, lunch, and evening snack
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "1 bowl of porridge with milk and a drizzle of honey\n1 boiled egg"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 cup of green tea with 2 whole wheat crackers"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Lunch",
                                content = "1 chapati with daal (lentils)\n1 small bowl of kachumber salad (onion, tomato, cucumber, lemon)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Evening Snack",
                                content = "1 glass of lemon water with a spoon of chia seeds"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Dinner unlocks at 8:00 PM",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        else -> {
                            // After 8 PM, show everything
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "1 bowl of porridge with milk and a drizzle of honey\n1 boiled egg"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 cup of green tea with 2 whole wheat crackers"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Lunch",
                                content = "1 chapati with daal (lentils)\n1 small bowl of kachumber salad (onion, tomato, cucumber, lemon)"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Evening Snack",
                                content = "1 glass of lemon water with a spoon of chia seeds"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Dinner",
                                content = "Baked fish with herbs and spices\n1 small bowl of brown rice\nMixed salad with olive oil dressing"
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginnerDay6DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.rope_pull_down)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"A rope pull-down involves grasping a rope attached to a high pulley, pulling it down towards your chest while keeping your elbows close to your body, and then slowly returning to the starting position.\"",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        fontStyle = FontStyle.Italic,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentTime.isBefore(LocalTime.of(10, 0))) {
                        // Before 10 AM, only show breakfast
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 egg omelet with vegetables\n1 slice of whole wheat toast\n1 cup of milk or tea without sugar"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mid-Morning Snack unlocks at 10:00 AM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
                        // Between 10 AM and 1 PM, show breakfast and morning snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 egg omelet with vegetables\n1 slice of whole wheat toast\n1 cup of milk or tea without sugar"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 bowl of yogurt with seasonal fruits (e.g., papaya or apple)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lunch unlocks at 1:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
                        // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 egg omelet with vegetables\n1 slice of whole wheat toast\n1 cup of milk or tea without sugar"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 bowl of yogurt with seasonal fruits (e.g., papaya or apple)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "1 cup of boiled brown rice\nGrilled chicken breast with vegetables (broccoli, carrots, and beans)\n1 glass of mint lemonade (without sugar)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Evening Snack unlocks at 5:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
                        // Between 5 PM and 8 PM, show breakfast, snack, lunch, and evening snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 egg omelet with vegetables\n1 slice of whole wheat toast\n1 cup of milk or tea without sugar"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 bowl of yogurt with seasonal fruits (e.g., papaya or apple)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "1 cup of boiled brown rice\nGrilled chicken breast with vegetables (broccoli, carrots, and beans)\n1 glass of mint lemonade (without sugar)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Evening Snack",
                            content = "A handful of pumpkin seeds or sunflower seeds"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dinner unlocks at 8:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else {
                        // After 8 PM, show everything
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 egg omelet with vegetables\n1 slice of whole wheat toast\n1 cup of milk or tea without sugar"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 bowl of yogurt with seasonal fruits (e.g., papaya or apple)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "1 cup of boiled brown rice\nGrilled chicken breast with vegetables (broccoli, carrots, and beans)\n1 glass of mint lemonade (without sugar)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Evening Snack",
                            content = "A handful of pumpkin seeds or sunflower seeds"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Dinner",
                            content = "Grilled salmon with asparagus\n1 small baked potato\nSteamed vegetables (zucchini, spinach)"
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginnerDay7DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.single_arm_bent_over)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"A single-arm bent-over row involves bending over with one leg straight and the other knee bent, holding a weight in one hand, and pulling it up towards your chest while keeping your back straight.\"",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        fontStyle = FontStyle.Italic,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Displaying each meal based on time
                    if (currentTime.isBefore(LocalTime.of(10, 0))) {
                        // Before 10 AM, only show breakfast
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 whole wheat roti with a small portion of aloo bhujia (light on oil)\n1 cup of lassi"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mid-Morning Snack unlocks at 10:00 AM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
                        // Between 10 AM and 1 PM, show breakfast and morning snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 whole wheat roti with a small portion of aloo bhujia (light on oil)\n1 cup of lassi"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 cup green tea with a handful of dried fruit (raisins, almonds)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lunch unlocks at 1:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
                        // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 whole wheat roti with a small portion of aloo bhujia (light on oil)\n1 cup of lassi"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 cup green tea with a handful of dried fruit (raisins, almonds)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Chicken curry with 1 chapati or brown rice\nMixed vegetable salad with lemon dressing"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Evening Snack unlocks at 5:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
                        // Between 5 PM and 8 PM, show breakfast, snack, lunch, and evening snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 whole wheat roti with a small portion of aloo bhujia (light on oil)\n1 cup of lassi"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 cup green tea with a handful of dried fruit (raisins, almonds)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Chicken curry with 1 chapati or brown rice\nMixed vegetable salad with lemon dressing"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Evening Snack",
                            content = "1 bowl of mixed fruit salad"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dinner unlocks at 8:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else {
                        // After 8 PM, show everything
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 whole wheat roti with a small portion of aloo bhujia (light on oil)\n1 cup of lassi"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 cup green tea with a handful of dried fruit (raisins, almonds)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Chicken curry with 1 chapati or brown rice\nMixed vegetable salad with lemon dressing"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Evening Snack",
                            content = "1 bowl of mixed fruit salad"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Dinner",
                            content = "1 chapati with daal\n1 small bowl of yogurt with a sprinkle of cumin seeds"
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntermediateDay1DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF025D93),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.sit_twist_exercise)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Sit tall, engage your core, and twist your torso from side to side, keeping your hips and legs still.\"",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Italic,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Displaying each meal based on time
                    if (currentTime.isBefore(LocalTime.of(10, 0))) {
                        // Before 10 AM, only show breakfast
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 boiled eggs\n2 slices of whole wheat toast\n1 glass of milk or lassi (yogurt-based drink)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mid-Morning Snack unlocks at 10:00 AM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
                        // Between 10 AM and 1 PM, show breakfast and morning snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 boiled eggs\n2 slices of whole wheat toast\n1 glass of milk or lassi (yogurt-based drink)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 banana or an apple"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lunch unlocks at 1:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
                        // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 boiled eggs\n2 slices of whole wheat toast\n1 glass of milk or lassi (yogurt-based drink)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 banana or an apple"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "1 cup chicken curry (cooked with minimal oil)\n1 serving of brown rice or 2 chapatis\nMixed vegetable salad (cucumber, tomatoes, carrots)\n1 small bowl of yogurt"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Afternoon Snack unlocks at 5:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
                        // Between 5 PM and 8 PM, show breakfast, snack, lunch, and afternoon snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 boiled eggs\n2 slices of whole wheat toast\n1 glass of milk or lassi (yogurt-based drink)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 banana or an apple"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "1 cup chicken curry (cooked with minimal oil)\n1 serving of brown rice or 2 chapatis\nMixed vegetable salad (cucumber, tomatoes, carrots)\n1 small bowl of yogurt"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "Handful of almonds or peanuts\n1 cup green tea or black tea without sugar"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dinner unlocks at 8:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else {
                        // After 8 PM, show everything
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 boiled eggs\n2 slices of whole wheat toast\n1 glass of milk or lassi (yogurt-based drink)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 banana or an apple"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "1 cup chicken curry (cooked with minimal oil)\n1 serving of brown rice or 2 chapatis\nMixed vegetable salad (cucumber, tomatoes, carrots)\n1 small bowl of yogurt"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "Handful of almonds or peanuts\n1 cup green tea or black tea without sugar"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Dinner",
                            content = "Grilled fish or chicken breast\nSteamed vegetables\n1 small serving of quinoa or whole wheat pasta\n1 small bowl of mixed fruit salad"
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntermediateDay2DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.situps)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Lie on your back, bend your knees, and place your hands behind your head. Lift your upper body off the floor, touching your elbows to your knees.\"",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        fontStyle = FontStyle.Italic,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Displaying each meal based on time
                    if (currentTime.isBefore(LocalTime.of(10, 0))) {
                        // Before 10 AM, only show breakfast
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 bowl of oatmeal with honey and nuts\n1 boiled egg\n1 cup of chai (with or without milk)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mid-Morning Snack unlocks at 10:00 AM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
                        // Between 10 AM and 1 PM, show breakfast and morning snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 bowl of oatmeal with honey and nuts\n1 boiled egg\n1 cup of chai (with or without milk)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 orange or grapefruit"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lunch unlocks at 1:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
                        // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 bowl of oatmeal with honey and nuts\n1 boiled egg\n1 cup of chai (with or without milk)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 orange or grapefruit"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled chicken or beef kebabs (150g)\n2 chapatis or 1 serving of brown rice\nCucumber raita (yogurt with cucumber)\nMixed green salad"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Afternoon Snack unlocks at 5:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
                        // Between 5 PM and 8 PM, show breakfast, snack, lunch, and afternoon snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 bowl of oatmeal with honey and nuts\n1 boiled egg\n1 cup of chai (with or without milk)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 orange or grapefruit"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled chicken or beef kebabs (150g)\n2 chapatis or 1 serving of brown rice\nCucumber raita (yogurt with cucumber)\nMixed green salad"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 cup of yogurt with honey\nA handful of roasted chickpeas (channa)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dinner unlocks at 8:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else {
                        // After 8 PM, show everything
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 bowl of oatmeal with honey and nuts\n1 boiled egg\n1 cup of chai (with or without milk)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 orange or grapefruit"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled chicken or beef kebabs (150g)\n2 chapatis or 1 serving of brown rice\nCucumber raita (yogurt with cucumber)\nMixed green salad"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 cup of yogurt with honey\nA handful of roasted chickpeas (channa)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Dinner",
                            content = "1 bowl of lentil soup (daal)\n1 roti or naan\nGrilled or steamed vegetables"
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntermediateDay3DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.twist)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Lie on your back, bend knees, place hands behind head, lift shoulders, twist upper body to right, hold, repeat on left.\"",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        fontStyle = FontStyle.Italic,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Displaying each meal based on time
                    if (currentTime.isBefore(LocalTime.of(10, 0))) {
                        // Before 10 AM, only show breakfast
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 paratha (cooked with minimal oil)\n1 serving of scrambled eggs\n1 cup of tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mid-Morning Snack unlocks at 10:00 AM",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                            fontSize = 18.sp
                        )
                    } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
                        // Between 10 AM and 1 PM, show breakfast and morning snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 paratha (cooked with minimal oil)\n1 serving of scrambled eggs\n1 cup of tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 cup of fresh juice (carrot or orange)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lunch unlocks at 1:00 PM",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                            fontSize = 18.sp
                        )
                    } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
                        // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 paratha (cooked with minimal oil)\n1 serving of scrambled eggs\n1 cup of tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 cup of fresh juice (carrot or orange)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled chicken or fish (150g)\n2 chapatis or 1 serving of brown rice\nFresh salad (lettuce, cucumbers, and tomatoes)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Afternoon Snack unlocks at 5:00 PM",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                            fontSize = 18.sp
                        )
                    } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
                        // Between 5 PM and 8 PM, show breakfast, snack, lunch, and afternoon snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 paratha (cooked with minimal oil)\n1 serving of scrambled eggs\n1 cup of tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 cup of fresh juice (carrot or orange)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled chicken or fish (150g)\n2 chapatis or 1 serving of brown rice\nFresh salad (lettuce, cucumbers, and tomatoes)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 cup of buttermilk (lassi without sugar)\n1 handful of mixed nuts"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dinner unlocks at 8:00 PM",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                            fontSize = 18.sp
                        )
                    } else {
                        // After 8 PM, show everything
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 paratha (cooked with minimal oil)\n1 serving of scrambled eggs\n1 cup of tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 cup of fresh juice (carrot or orange)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled chicken or fish (150g)\n2 chapatis or 1 serving of brown rice\nFresh salad (lettuce, cucumbers, and tomatoes)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 cup of buttermilk (lassi without sugar)\n1 handful of mixed nuts"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Dinner",
                            content = "Chicken or mutton pulao (small portion)\nMixed vegetable curry or a side of sautéed greens\n1 serving of yogurt"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom padding
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntermediateDay4DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.parallel_dip_bar)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Hanging from parallel bars with your arms straight and then lowering your body until your elbows are at a 90-degree angle before pushing back up.\"",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        fontStyle = FontStyle.Italic,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))


// Displaying each meal based on time
                    if (currentTime.isBefore(LocalTime.of(10, 0))) {
                        // Before 10 AM, only show breakfast
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast:",
                            content = "2 boiled eggs\n2 slices of whole wheat toast\n1 cup of black tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mid-Morning Snack unlocks at 10:00 AM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
                        // Between 10 AM and 1 PM, show breakfast and morning snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast:",
                            content = "2 boiled eggs\n2 slices of whole wheat toast\n1 cup of black tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack:",
                            content = "1 apple or pear"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lunch unlocks at 1:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
                        // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast:",
                            content = "2 boiled eggs\n2 slices of whole wheat toast\n1 cup of black tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack:",
                            content = "1 apple or pear"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch:",
                            content = "Beef or chicken curry (150g, cooked with minimal oil)\n1 serving of boiled brown rice\nFresh salad with lemon and olive oil dressing"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Afternoon Snack unlocks at 5:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
                        // Between 5 PM and 8 PM, show breakfast, snack, lunch, and afternoon snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast:",
                            content = "2 boiled eggs\n2 slices of whole wheat toast\n1 cup of black tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack:",
                            content = "1 apple or pear"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch:",
                            content = "Beef or chicken curry (150g, cooked with minimal oil)\n1 serving of boiled brown rice\nFresh salad with lemon and olive oil dressing"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack:",
                            content = "1 cup of yogurt with flaxseeds\nA handful of dates"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dinner unlocks at 8:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else {
                        // After 8 PM, show everything
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast:",
                            content = "2 boiled eggs\n2 slices of whole wheat toast\n1 cup of black tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack:",
                            content = "1 apple or pear"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch:",
                            content = "Beef or chicken curry (150g, cooked with minimal oil)\n1 serving of boiled brown rice\nFresh salad with lemon and olive oil dressing"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack:",
                            content = "1 cup of yogurt with flaxseeds\nA handful of dates"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Dinner:",
                            content = "Grilled or baked fish (150g)\n1 roti or a small portion of rice\nMixed vegetables (steamed or sautéed)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Before Bed:",
                            content = "1 glass of warm milk or herbal tea"
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntermediateDay5DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.plankroll)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black) // Set to Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Involves starting in a plank position, then rotating your body sideways, transferring your weight to one forearm, and then rolling to the opposite side, keeping your body in a straight line throughout.\"",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black), // Set to Black
                        fontStyle = FontStyle.Italic,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black) // Set to Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Displaying each meal based on time
                    if (currentTime.isBefore(LocalTime.of(10, 0))) {
                        // Before 10 AM, only show breakfast
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 bowl of porridge (daliya) with honey\n1 boiled egg\n1 cup of chai (optional)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mid-Morning Snack unlocks at 10:00 AM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
                        // Between 10 AM and 1 PM, show breakfast and morning snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 bowl of porridge (daliya) with honey\n1 boiled egg\n1 cup of chai (optional)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 handful of almonds or walnuts"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lunch unlocks at 1:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
                        // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 bowl of porridge (daliya) with honey\n1 boiled egg\n1 cup of chai (optional)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 handful of almonds or walnuts"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Chicken or mutton biryani (small portion)\nRaita or plain yogurt\nFresh salad"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Afternoon Snack unlocks at 5:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
                        // Between 5 PM and 8 PM, show breakfast, snack, lunch, and afternoon snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 bowl of porridge (daliya) with honey\n1 boiled egg\n1 cup of chai (optional)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 handful of almonds or walnuts"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Chicken or mutton biryani (small portion)\nRaita or plain yogurt\nFresh salad"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 cup of green tea or black tea\nA handful of roasted nuts or seeds"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dinner unlocks at 8:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else {
                        // After 8 PM, show everything
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 bowl of porridge (daliya) with honey\n1 boiled egg\n1 cup of chai (optional)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 handful of almonds or walnuts"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Chicken or mutton biryani (small portion)\nRaita or plain yogurt\nFresh salad"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 cup of green tea or black tea\nA handful of roasted nuts or seeds"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Dinner",
                            content = "Grilled fish or chicken\nSteamed vegetables\nBrown rice or whole-wheat chapati"
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntermediateDay6DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.single_arm_bent_over)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black) // Set to Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"A single-arm bent-over row involves bending over at the waist, holding a dumbbell in one hand, and pulling it up towards your chest while keeping your back straight.\"",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black), // Set to Black
                        fontStyle = FontStyle.Italic,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black) // Set to Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Displaying each meal based on time
                    if (currentTime.isBefore(LocalTime.of(10, 0))) {
                        // Before 10 AM, only show breakfast
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 stuffed paratha (stuffed with potatoes or spinach)\n1 boiled egg\n1 cup of tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mid-Morning Snack unlocks at 10:00 AM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray) // Set to Gray
                        )
                    } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
                        // Between 10 AM and 1 PM, show breakfast and morning snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 stuffed paratha (stuffed with potatoes or spinach)\n1 boiled egg\n1 cup of tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 orange or apple"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lunch unlocks at 1:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray) // Set to Gray
                        )
                    } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
                        // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 stuffed paratha (stuffed with potatoes or spinach)\n1 boiled egg\n1 cup of tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 orange or apple"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled chicken or beef kebabs (150g)\n2 chapatis or 1 serving of brown rice\nFresh salad (lettuce, cucumber, carrots)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Afternoon Snack unlocks at 5:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray) // Set to Gray
                        )
                    } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
                        // Between 5 PM and 8 PM, show breakfast, snack, lunch, and afternoon snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 stuffed paratha (stuffed with potatoes or spinach)\n1 boiled egg\n1 cup of tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 orange or apple"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled chicken or beef kebabs (150g)\n2 chapatis or 1 serving of brown rice\nFresh salad (lettuce, cucumber, carrots)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 cup of yogurt with honey or fruit\nA handful of mixed nuts"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dinner unlocks at 8:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray) // Set to Gray
                        )
                    } else {
                        // After 8 PM, show everything
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 stuffed paratha (stuffed with potatoes or spinach)\n1 boiled egg\n1 cup of tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 orange or apple"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled chicken or beef kebabs (150g)\n2 chapatis or 1 serving of brown rice\nFresh salad (lettuce, cucumber, carrots)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 cup of yogurt with honey or fruit\nA handful of mixed nuts"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Dinner",
                            content = "Grilled fish or baked chicken (150g)\nSteamed vegetables\n1 chapati or 1 serving of brown rice"
                        )
                    }

                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntermediateDay7DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.lunges)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Step forward and bend both knees, lowering until your knees are bent at a 90-degree angle.\"",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        fontStyle = FontStyle.Italic,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentTime.isBefore(LocalTime.of(10, 0))) {
                        // Before 10 AM, only show breakfast
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 boiled eggs\n1 slice of whole wheat bread\n1 cup of black tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mid-Morning Snack unlocks at 10:00 AM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
                        // Between 10 AM and 1 PM, show breakfast and morning snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 boiled eggs\n1 slice of whole wheat bread\n1 cup of black tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 banana or a small bowl of mixed fruits"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lunch unlocks at 1:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
                        // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 boiled eggs\n1 slice of whole wheat bread\n1 cup of black tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 banana or a small bowl of mixed fruits"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Chicken tikka or grilled fish (150g)\n1 roti or a small portion of pulao\nFresh salad with lemon juice"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Afternoon Snack unlocks at 5:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
                        // Between 5 PM and 8 PM, show breakfast, snack, lunch, and afternoon snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 boiled eggs\n1 slice of whole wheat bread\n1 cup of black tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 banana or a small bowl of mixed fruits"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Chicken tikka or grilled fish (150g)\n1 roti or a small portion of pulao\nFresh salad with lemon juice"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 cup of buttermilk or lassi\nA handful of roasted chickpeas"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dinner unlocks at 8:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else {
                        // After 8 PM, show everything
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 boiled eggs\n1 slice of whole wheat bread\n1 cup of black tea or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 banana or a small bowl of mixed fruits"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Chicken tikka or grilled fish (150g)\n1 roti or a small portion of pulao\nFresh salad with lemon juice"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 cup of buttermilk or lassi\nA handful of roasted chickpeas"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Dinner",
                            content = "Lentil soup or chicken soup\n1 chapati or naan\nSteamed vegetables (broccoli, carrots, spinach)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Before Bed",
                            content = "1 small bowl of yogurt or a glass of warm milk"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GifImageFromDrawable(drawableResId: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            ImageView(ctx).apply {
                Glide.with(ctx)
                    .asGif()
                    .load(drawableResId)
                    .apply(RequestOptions()) // Optional: adjust the size if needed
                    .into(this)
            }
        },
        modifier = modifier
    )
}

@Composable
fun StyledTextWithBoldHeadings(
    heading: String,
    content: String
) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp , fontStyle = FontStyle.Italic)) {
                append("$heading\n")
            }
            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                append(content)
            }
        },
        fontSize = 18.sp, // Set the default font size for the entire text
        color = Color.Black
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedDay1DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.hangging_leg_raises)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Hang from a bar with an overhand grip, engage your core, and raise your legs towards your chest, keeping your body straight.\"",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentTime.isBefore(LocalTime.of(10, 0))) {
                        // Before 10 AM, only show breakfast
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "3 Boiled eggs (or omelette with spinach and tomatoes)\n1 whole wheat paratha (lightly brushed with olive oil)\n1 cup of green tea or black coffee"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mid-Morning Snack unlocks at 10:00 AM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
                        // Between 10 AM and 1 PM, show breakfast and morning snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "3 Boiled eggs (or omelette with spinach and tomatoes)\n1 whole wheat paratha (lightly brushed with olive oil)\n1 cup of green tea or black coffee"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 handful of almonds\n1 cup of low-fat yogurt"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lunch unlocks at 1:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
                        // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "3 Boiled eggs (or omelette with spinach and tomatoes)\n1 whole wheat paratha (lightly brushed with olive oil)\n1 cup of green tea or black coffee"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 handful of almonds\n1 cup of low-fat yogurt"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled chicken breast with a side of mixed vegetables (broccoli, carrots, bell peppers)\n1 serving of whole wheat chapati or brown rice"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Afternoon Snack unlocks at 5:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
                        // Between 5 PM and 8 PM, show breakfast, snack, lunch, and afternoon snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "3 Boiled eggs (or omelette with spinach and tomatoes)\n1 whole wheat paratha (lightly brushed with olive oil)\n1 cup of green tea or black coffee"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 handful of almonds\n1 cup of low-fat yogurt"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled chicken breast with a side of mixed vegetables (broccoli, carrots, bell peppers)\n1 serving of whole wheat chapati or brown rice"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 cucumber sliced with a dash of lemon and black salt"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dinner unlocks at 8:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else {
                        // After 8 PM, show everything
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "3 Boiled eggs (or omelette with spinach and tomatoes)\n1 whole wheat paratha (lightly brushed with olive oil)\n1 cup of green tea or black coffee"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 handful of almonds\n1 cup of low-fat yogurt"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled chicken breast with a side of mixed vegetables (broccoli, carrots, bell peppers)\n1 serving of whole wheat chapati or brown rice"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 cucumber sliced with a dash of lemon and black salt"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Dinner",
                            content = "Fish curry (made with low-fat yogurt and spices)\n1 serving of quinoa or brown rice\nFresh green salad with olive oil dressing"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Post-Dinner",
                            content = "1 cup of herbal tea (such as chamomile or mint)"
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedDay2DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.incline_bench_row_excersise)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Lie face down on an incline bench, holding dumbbells with a neutral grip (palms facing inward).\"",
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    when {
                        currentTime.isBefore(LocalTime.of(10, 0)) -> {
                            // Before 10 AM, only show breakfast
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "Greek yogurt with mixed berries and a handful of walnuts\n1 slice of multigrain toast"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Mid-Morning Snack unlocks at 10:00 AM",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        currentTime.isBefore(LocalTime.of(13, 0)) -> {
                            // Between 10 AM and 1 PM, show breakfast and morning snack
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "Greek yogurt with mixed berries and a handful of walnuts\n1 slice of multigrain toast"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 apple or pear\nGreen tea"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Lunch unlocks at 1:00 PM",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        currentTime.isBefore(LocalTime.of(17, 0)) -> {
                            // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "Greek yogurt with mixed berries and a handful of walnuts\n1 slice of multigrain toast"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 apple or pear\nGreen tea"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Lunch",
                                content = "Grilled lean beef kebabs with a side of sautéed spinach\n1 small serving of brown rice"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Afternoon Snack unlocks at 5:00 PM",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        currentTime.isBefore(LocalTime.of(20, 0)) -> {
                            // Between 5 PM and 8 PM, show breakfast, snack, lunch, and afternoon snack
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "Greek yogurt with mixed berries and a handful of walnuts\n1 slice of multigrain toast"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 apple or pear\nGreen tea"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Lunch",
                                content = "Grilled lean beef kebabs with a side of sautéed spinach\n1 small serving of brown rice"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Afternoon Snack",
                                content = "1 boiled egg with a sprinkle of black pepper and salt"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Dinner unlocks at 8:00 PM",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        else -> {
                            // After 8 PM, show everything
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "Greek yogurt with mixed berries and a handful of walnuts\n1 slice of multigrain toast"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 apple or pear\nGreen tea"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Lunch",
                                content = "Grilled lean beef kebabs with a side of sautéed spinach\n1 small serving of brown rice"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Afternoon Snack",
                                content = "1 boiled egg with a sprinkle of black pepper and salt"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Dinner",
                                content = "Chicken tikka with roasted vegetables (carrots, zucchini, eggplant)\n1 whole wheat chapati"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Post-Dinner",
                                content = "1 cup of low-fat milk"
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedDay3DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.dips_on_parallel_bar)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Position your hands shoulder-width apart on the parallel bars, lower your body until your elbows are at a 90-degree angle, and push back up to the starting position.\"",
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentTime.isBefore(LocalTime.of(10, 0))) {
                        // Before 10 AM, only show breakfast
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "Oatmeal cooked with almond milk, topped with chia seeds and sliced bananas\nBlack coffee or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mid-Morning Snack unlocks at 10:00 AM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
                        // Between 10 AM and 1 PM, show breakfast and morning snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "Oatmeal cooked with almond milk, topped with chia seeds and sliced bananas\nBlack coffee or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 handful of mixed nuts (almonds, walnuts, pistachios)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lunch unlocks at 1:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
                        // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "Oatmeal cooked with almond milk, topped with chia seeds and sliced bananas\nBlack coffee or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 handful of mixed nuts (almonds, walnuts, pistachios)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Lentil (daal) soup with a side of sautéed vegetables\n1 serving of quinoa or brown rice"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Afternoon Snack unlocks at 5:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
                        // Between 5 PM and 8 PM, show breakfast, snack, lunch, and afternoon snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "Oatmeal cooked with almond milk, topped with chia seeds and sliced bananas\nBlack coffee or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 handful of mixed nuts (almonds, walnuts, pistachios)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Lentil (daal) soup with a side of sautéed vegetables\n1 serving of quinoa or brown rice"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 small bowl of mixed fruit (papaya, guava, oranges)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dinner unlocks at 8:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else {
                        // After 8 PM, show everything
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "Oatmeal cooked with almond milk, topped with chia seeds and sliced bananas\nBlack coffee or green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 handful of mixed nuts (almonds, walnuts, pistachios)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Lentil (daal) soup with a side of sautéed vegetables\n1 serving of quinoa or brown rice"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 small bowl of mixed fruit (papaya, guava, oranges)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Dinner",
                            content = "Grilled salmon with a side of steamed broccoli and bell peppers\nFresh salad with a drizzle of olive oil"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Post-Dinner",
                            content = "Herbal tea (ginger or mint)"
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedDay4DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.dumbbell_flat_bench_press)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Lie on a flat bench, hold dumbbells with your palms facing forward, and press them up towards the ceiling until your arms are fully extended.\"",
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentTime.isBefore(LocalTime.of(10, 0))) {
                        // Before 10 AM, only show breakfast
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 scrambled eggs with onions and bell peppers\n1 whole wheat paratha\nBlack coffee"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mid-Morning Snack unlocks at 10:00 AM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
                        // Between 10 AM and 1 PM, show breakfast and morning snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 scrambled eggs with onions and bell peppers\n1 whole wheat paratha\nBlack coffee"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 glass of fresh vegetable juice (carrot, beetroot, and cucumber)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lunch unlocks at 1:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
                        // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 scrambled eggs with onions and bell peppers\n1 whole wheat paratha\nBlack coffee"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 glass of fresh vegetable juice (carrot, beetroot, and cucumber)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Paneer tikka with a side of sautéed spinach and mushrooms\n1 small serving of brown rice or quinoa"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Afternoon Snack unlocks at 5:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
                        // Between 5 PM and 8 PM, show breakfast, snack, lunch, and afternoon snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 scrambled eggs with onions and bell peppers\n1 whole wheat paratha\nBlack coffee"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 glass of fresh vegetable juice (carrot, beetroot, and cucumber)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Paneer tikka with a side of sautéed spinach and mushrooms\n1 small serving of brown rice or quinoa"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 handful of sunflower seeds or pumpkin seeds"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dinner unlocks at 8:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else {
                        // After 8 PM, show everything
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 scrambled eggs with onions and bell peppers\n1 whole wheat paratha\nBlack coffee"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 glass of fresh vegetable juice (carrot, beetroot, and cucumber)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Paneer tikka with a side of sautéed spinach and mushrooms\n1 small serving of brown rice or quinoa"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 handful of sunflower seeds or pumpkin seeds"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Dinner",
                            content = "Chicken shashlik with grilled vegetables (onions, tomatoes, bell peppers)\n1 serving of whole wheat chapati"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Post-Dinner",
                            content = "1 cup of warm low-fat milk with a pinch of turmeric"
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedDay5DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.dumbbell_weight_leggpull)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Stand with feet shoulder-width apart, hold dumbbells at your sides, hinge at the hips, lower weights towards the floor, keeping back straight, then reverse the movement.\"",
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentTime.isBefore(LocalTime.of(10, 0))) {
                        // Before 10 AM, only show breakfast
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 smoothie made with spinach, banana, and almond milk\n1 boiled egg"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mid-Morning Snack unlocks at 10:00 AM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
                        // Between 10 AM and 1 PM, show breakfast and morning snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 smoothie made with spinach, banana, and almond milk\n1 boiled egg"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 handful of almonds and cashews"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lunch unlocks at 1:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
                        // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 smoothie made with spinach, banana, and almond milk\n1 boiled egg"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 handful of almonds and cashews"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled chicken salad with mixed greens, cucumber, tomatoes, and olive oil dressing\n1 small serving of brown rice"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Afternoon Snack unlocks at 5:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
                        // Between 5 PM and 8 PM, show breakfast, snack, lunch, and afternoon snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 smoothie made with spinach, banana, and almond milk\n1 boiled egg"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 handful of almonds and cashews"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled chicken salad with mixed greens, cucumber, tomatoes, and olive oil dressing\n1 small serving of brown rice"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 small bowl of yogurt with honey"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dinner unlocks at 8:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else {
                        // After 8 PM, show everything
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "1 smoothie made with spinach, banana, and almond milk\n1 boiled egg"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 handful of almonds and cashews"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled chicken salad with mixed greens, cucumber, tomatoes, and olive oil dressing\n1 small serving of brown rice"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 small bowl of yogurt with honey"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Dinner",
                            content = "Fish curry (made with coconut milk) with a side of mixed vegetables\n1 small serving of quinoa or brown rice"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Post-Dinner",
                            content = "Herbal tea (such as chamomile or mint)"
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedDay6DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.hangging_knee_raise)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Hang from a pull-up bar with an overhand grip, engage your core, and raise your knees towards your chest, keeping your body straight.\"",
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentTime.isBefore(LocalTime.of(10, 0))) {
                        // Before 10 AM, only show breakfast
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 boiled eggs with 1 whole wheat toast\n1 cup of green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mid-Morning Snack unlocks at 10:00 AM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(13, 0))) {
                        // Between 10 AM and 1 PM, show breakfast and morning snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 boiled eggs with 1 whole wheat toast\n1 cup of green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 orange or grapefruit"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lunch unlocks at 1:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(17, 0))) {
                        // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 boiled eggs with 1 whole wheat toast\n1 cup of green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 orange or grapefruit"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled mutton kebabs with a side of roasted vegetables\n1 small serving of brown rice or chapati"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Afternoon Snack unlocks at 5:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
                        // Between 5 PM and 8 PM, show breakfast, snack, lunch, and afternoon snack
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 boiled eggs with 1 whole wheat toast\n1 cup of green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 orange or grapefruit"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled mutton kebabs with a side of roasted vegetables\n1 small serving of brown rice or chapati"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 small bowl of mixed fruit (apples, bananas, berries)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dinner unlocks at 8:00 PM",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    } else {
                        // After 8 PM, show everything
                        StyledTextWithBoldHeadings(
                            heading = "Breakfast",
                            content = "2 boiled eggs with 1 whole wheat toast\n1 cup of green tea"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Mid-Morning Snack",
                            content = "1 orange or grapefruit"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Lunch",
                            content = "Grilled mutton kebabs with a side of roasted vegetables\n1 small serving of brown rice or chapati"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Afternoon Snack",
                            content = "1 small bowl of mixed fruit (apples, bananas, berries)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Dinner",
                            content = "Baked chicken with a side of sautéed spinach and green beans\nFresh salad with olive oil and lemon dressing"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextWithBoldHeadings(
                            heading = "Post-Dinner",
                            content = "1 cup of warm low-fat milk"
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedDay7DetailedWorkoutScreen(navController: NavController) {
    val currentTime = remember { LocalTime.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Box with an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(300.dp)
                    .background(Color(0xffefeff8))
            ) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            Glide.with(ctx)
                                .asGif()
                                .load(R.drawable.hangging_knee_raise)
                                .apply(RequestOptions()) // Optional: adjust the size if needed
                                .into(this)
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for workout details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workout",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Hang from a bar with an overhand grip, engage your core, and raise your legs towards your chest, keeping your body straight.\"",
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for diet plan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diet Plan",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    when {
                        currentTime.isBefore(LocalTime.of(10, 0)) -> {
                            // Before 10 AM, only show breakfast
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "1 bowl of mixed fruit salad (melon, papaya, guava) with a handful of nuts\nBlack coffee"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Mid-Morning Snack unlocks at 10:00 AM",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }

                        currentTime.isBefore(LocalTime.of(13, 0)) -> {
                            // Between 10 AM and 1 PM, show breakfast and morning snack
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "1 bowl of mixed fruit salad (melon, papaya, guava) with a handful of nuts\nBlack coffee"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 small bowl of yogurt with chia seeds"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Lunch unlocks at 1:00 PM",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }

                        currentTime.isBefore(LocalTime.of(17, 0)) -> {
                            // Between 1 PM and 5 PM, show breakfast, snack, and lunch
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "1 bowl of mixed fruit salad (melon, papaya, guava) with a handful of nuts\nBlack coffee"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 small bowl of yogurt with chia seeds"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Lunch",
                                content = "Grilled shrimp with a side of steamed vegetables (broccoli, carrots, and bell peppers)\n1 small serving of brown rice or chapati"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Afternoon Snack unlocks at 5:00 PM",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }

                        currentTime.isBefore(LocalTime.of(20, 0)) -> {
                            // Between 5 PM and 8 PM, show breakfast, snack, lunch, and afternoon snack
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "1 bowl of mixed fruit salad (melon, papaya, guava) with a handful of nuts\nBlack coffee"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 small bowl of yogurt with chia seeds"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Lunch",
                                content = "Grilled shrimp with a side of steamed vegetables (broccoli, carrots, and bell peppers)\n1 small serving of brown rice or chapati"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Afternoon Snack",
                                content = "1 boiled egg with a sprinkle of black pepper and salt"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Dinner unlocks at 8:00 PM",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }

                        else -> {
                            // After 8 PM, show everything
                            StyledTextWithBoldHeadings(
                                heading = "Breakfast",
                                content = "1 bowl of mixed fruit salad (melon, papaya, guava) with a handful of nuts\nBlack coffee"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Mid-Morning Snack",
                                content = "1 small bowl of yogurt with chia seeds"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Lunch",
                                content = "Grilled shrimp with a side of steamed vegetables (broccoli, carrots, and bell peppers)\n1 small serving of brown rice or chapati"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Afternoon Snack",
                                content = "1 boiled egg with a sprinkle of black pepper and salt"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Dinner",
                                content = "Chicken karahi (made with minimal oil and lots of tomatoes and spices)\nFresh green salad with a side of sautéed spinach\n1 small serving of whole wheat chapati"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledTextWithBoldHeadings(
                                heading = "Post-Dinner",
                                content = "Herbal tea (such as ginger or mint)"
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ExerciseCard(exerciseName: String, duration: String, imageContent: @Composable () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xffefeff8)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            imageContent()

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = exerciseName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF025D93)
                )
                Text(
                    text = duration,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF025D93)
                )
            }
        }
    }
}
