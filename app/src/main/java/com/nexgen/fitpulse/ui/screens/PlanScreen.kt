package com.nexgen.fitpulse.ui.screens

import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import com.nexgen.fitpulse.R
import com.nexgen.fitpulse.ui.components.BannerAdView
import com.nexgen.fitpulse.ui.viewmodel.UserSelectionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen(selectedItem: Int, onItemSelected: (Int) -> Unit, onBackClick: () -> Unit, viewModel: UserSelectionViewModel , navController: NavController) {
    val selectedExperience = viewModel.selectedExperience.value

    Scaffold(
        containerColor = Color(0xFF011C2D),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (selectedExperience) {
                            "Beginner" -> "Beginner Diet Plan"
                            "Intermediate" -> "Intermediate Diet Plan"
                            "Advanced" -> "Advanced Diet Plan"
                            else -> "Plans"
                        },
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
            when (selectedExperience) {
                "Beginner" -> BeginnerDietPlan(navController)     // Show Beginner Plan
                "Intermediate" -> IntermediateDietPlan(navController) // Show Intermediate Plan
                "Advanced" -> AdvancedDietPlan(navController)     // Show Advanced Plan
                else -> Text(
                    text = "Please select an experience level",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

val Context.dataStore by preferencesDataStore(name = "button_prefs")

@Composable
fun DietPlanCard(
    imageRes: Int,
    title: String,
    description: String,
    extraInfo: String,
    isEnabled: Boolean,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEnabled) { onClick() }
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) Color.White else Color.LightGray,
            contentColor = Color(0xFF025D93)
        )
    ) {
        Row(modifier = Modifier.padding(2.dp)) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = Color(0xFF025D93), fontWeight = FontWeight.Bold)
                Text(text = description, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF025D93))
                Text(text = extraInfo, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF025D93))
            }
        }
    }
}

@Composable
fun BeginnerDietPlan(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Keys for storing button states and timestamps
    var buttonUnlockTimestamps by remember { mutableStateOf<Map<Int, Long>>(emptyMap()) }
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Load button unlock timestamps from DataStore
    LaunchedEffect(Unit) {
        val preferences = context.dataStore.data.first()
        buttonUnlockTimestamps = (1..7).associateWith { day ->
            preferences[longPreferencesKey("beginner_day${day}_unlock_time")] ?: (System.currentTimeMillis() + TimeUnit.HOURS.toMillis(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        DietPlanCard(
            imageRes = R.drawable.diet_image1,
            title = "Day 1",
            description = "Calorie Burn",
            extraInfo = "2000 kcal",
            isEnabled = true, // Always enabled
            onClick = {
                coroutineScope.launch {
                    // Unlock subsequent cards one-by-one each day
                    (2..7).forEach { day ->
                        val unlockTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis((day - 1).toLong())
                        context.dataStore.edit {
                            it[longPreferencesKey("beginner_day${day}_unlock_time")] = unlockTime
                        }
                    }
                }
                navController.navigate("beg_detailed_day_1")
            }
        )

        for (day in 2..7) {
            DietPlanCard(
                imageRes = when (day) {
                    2 -> R.drawable.diet_image2
                    3 -> R.drawable.diet_image3
                    4 -> R.drawable.diet_image4
                    5 -> R.drawable.diet_image5
                    6 -> R.drawable.diet_image6
                    7 -> R.drawable.diet_image7
                    else -> R.drawable.diet_image1 // Fallback
                },
                title = "Day $day",
                description = "Calorie Burn",
                extraInfo = "2000 kcal",
                isEnabled = isCardUnlocked(day),
                onClick = {
                    if (isCardUnlocked(day)) {
                        navController.navigate("beg_detailed_day_$day")
                    }
                }
            )
        }
    }
}



@Composable
fun IntermediateDietPlan(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Keys for storing button states and timestamps
    var buttonUnlockTimestamps by remember { mutableStateOf<Map<Int, Long>>(emptyMap()) }
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Load button unlock timestamps from DataStore
    LaunchedEffect(Unit) {
        val preferences = context.dataStore.data.first()
        buttonUnlockTimestamps = (1..7).associateWith { day ->
            preferences[longPreferencesKey("beginner_day${day}_unlock_time")] ?: (System.currentTimeMillis() + TimeUnit.HOURS.toMillis(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        DietPlanCard(
            imageRes = R.drawable.diet_image1,
            title = "Day 1",
            description = "Calorie Burn",
            extraInfo = "2000 kcal",
            isEnabled = true, // Always enabled
            onClick = {
                coroutineScope.launch {
                    // Unlock subsequent cards one-by-one each day
                    (2..7).forEach { day ->
                        val unlockTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis((day - 1).toLong())
                        context.dataStore.edit {
                            it[longPreferencesKey("beginner_day${day}_unlock_time")] = unlockTime
                        }
                    }
                }
                navController.navigate("beg_detailed_day_1")
            }
        )

        for (day in 2..7) {
            DietPlanCard(
                imageRes = when (day) {
                    2 -> R.drawable.diet_image2
                    3 -> R.drawable.diet_image3
                    4 -> R.drawable.diet_image4
                    5 -> R.drawable.diet_image5
                    6 -> R.drawable.diet_image6
                    7 -> R.drawable.diet_image7
                    else -> R.drawable.diet_image1 // Fallback
                },
                title = "Day $day",
                description = "Calorie Burn",
                extraInfo = "2000 kcal",
                isEnabled = isCardUnlocked(day),
                onClick = {
                    if (isCardUnlocked(day)) {
                        navController.navigate("int_detailed_day_$day")
                    }
                }
            )
        }
    }
}


@Composable
fun AdvancedDietPlan(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Keys for storing button states and timestamps
    var buttonUnlockTimestamps by remember { mutableStateOf<Map<Int, Long>>(emptyMap()) }
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Load button unlock timestamps from DataStore
    LaunchedEffect(Unit) {
        val preferences = context.dataStore.data.first()
        buttonUnlockTimestamps = (1..7).associateWith { day ->
            preferences[longPreferencesKey("beginner_day${day}_unlock_time")] ?: (System.currentTimeMillis() + TimeUnit.HOURS.toMillis(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        DietPlanCard(
            imageRes = R.drawable.diet_image1,
            title = "Day 1",
            description = "Calorie Burn",
            extraInfo = "2000 kcal",
            isEnabled = true, // Always enabled
            onClick = {
                coroutineScope.launch {
                    // Unlock subsequent cards one-by-one each day
                    (2..7).forEach { day ->
                        val unlockTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis((day - 1).toLong())
                        context.dataStore.edit {
                            it[longPreferencesKey("beginner_day${day}_unlock_time")] = unlockTime
                        }
                    }
                }
                navController.navigate("beg_detailed_day_1")
            }
        )

        for (day in 2..7) {
            DietPlanCard(
                imageRes = when (day) {
                    2 -> R.drawable.diet_image2
                    3 -> R.drawable.diet_image3
                    4 -> R.drawable.diet_image4
                    5 -> R.drawable.diet_image5
                    6 -> R.drawable.diet_image6
                    7 -> R.drawable.diet_image7
                    else -> R.drawable.diet_image1 // Fallback
                },
                title = "Day $day",
                description = "Calorie Burn",
                extraInfo = "2000 kcal",
                isEnabled = isCardUnlocked(day),
                onClick = {
                    if (isCardUnlocked(day)) {
                        navController.navigate("adv_detailed_day_$day")
                    }
                }
            )
        }
    }
}


