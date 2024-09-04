package com.nexgen.fitnest.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexgen.fitnest.R
import com.nexgen.fitnest.data.viewmodel.UserViewModel
import com.nexgen.fitnest.ui.components.AppBackground
import com.nexgen.fitnest.ui.components.BannerAdView
import com.nexgen.fitnest.ui.components.BottomNavigationBar
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit // Callback for the back button
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var phoneNumber by remember { mutableStateOf<String?>(null) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var aboutText by remember { mutableStateOf("") }
    var isEditingAbout by remember { mutableStateOf(false) }
    var showAboutPrompt by remember { mutableStateOf(false) }

    // Fetch user details
    LaunchedEffect(Unit) {
        userViewModel.getUserPhoneNumber { fetchedPhoneNumber ->
            phoneNumber = fetchedPhoneNumber
        }
        firstName = sharedPreferences.getString("firstName", "") ?: ""
        lastName = sharedPreferences.getString("lastName", "") ?: ""
        email = sharedPreferences.getString("email", "") ?: ""
        aboutText = sharedPreferences.getString("aboutText", "") ?: ""

        // Check if this is the user's first visit and prompt for "About" info if needed
        if (aboutText.isEmpty()) {
            showAboutPrompt = true
        }

        // Debugging: Log the email to ensure it's being loaded
        Log.d("ProfileScreen", "Loaded email: $email")
    }

    var profileImage by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(Unit) {
        val savedImagePath = sharedPreferences.getString("profileImagePath", null)
        savedImagePath?.let {
            try {
                val file = File(it)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    profileImage = bitmap.asImageBitmap()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Save updated details to shared preferences
    fun saveProfileUpdates() {
        sharedPreferences.edit().apply {
            putString("firstName", firstName)
            putString("lastName", lastName)
            putString("email", email)
            putString("aboutText", aboutText)
            apply()
        }
    }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                try {
                    val imageFile = File(context.filesDir, "profile_image.jpg")
                    val inputStream = context.contentResolver.openInputStream(it)
                    inputStream?.use { stream ->
                        val outputStream = FileOutputStream(imageFile)
                        stream.copyTo(outputStream)
                        outputStream.flush()
                        outputStream.close()
                    }

                    sharedPreferences.edit().putString("profileImagePath", imageFile.absolutePath)
                        .apply()

                    val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                    profileImage = bitmap.asImageBitmap()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Profile", color = Color.White, fontSize = 26.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Column {
                // Add BannerAdView first so it appears above the BottomNavigationBar
                BannerAdView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    adUnitId = "ca-app-pub-3940256099942544/6300978111" // Replace with your actual Ad Unit ID
                )
                // Then add BottomNavigationBar
                BottomNavigationBar(
                    selectedItem = 0, // Mark Profile as selected
                    onItemSelected = {
                        when (it) {
                            2 -> onNavigateToSettings()
                            1 -> onNavigateToHome()
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile picture and information
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(128.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            profileImage?.let {
                                Image(
                                    bitmap = it,
                                    contentDescription = "Profile Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(128.dp)
                                        .clip(CircleShape)
                                        .background(
                                            MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                )
                            } ?: run {
                                Image(
                                    painter = painterResource(id = R.drawable.person_icon),
                                    contentDescription = "Profile Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(128.dp)
                                        .clip(CircleShape)
                                        .background(
                                            MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                )
                            }

                            IconButton(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White, CircleShape)
                                    .align(Alignment.BottomEnd)
                                    .offset(x = (0).dp, y = (0).dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.cameraicon),
                                    contentDescription = "Edit Image",
                                    modifier = Modifier.size(30.dp),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))

                        // Name and Phone number display
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(Color.Transparent)
                                .border(1.dp, Color.White, shape = RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$firstName $lastName",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Email display
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(Color.Transparent)
                                .border(1.dp, Color.White, shape = RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (email.isNotEmpty()) email else "Email not available",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // About section
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(Color.Transparent)
                                .border(1.dp, Color.White, shape = RoundedCornerShape(8.dp))
                                .clickable {
                                    // Enable editing mode when box is clicked
                                    isEditingAbout = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isEditingAbout) {
                                TextField(
                                    value = aboutText,
                                    onValueChange = { aboutText = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Transparent),
                                    colors = TextFieldDefaults.textFieldColors(
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        containerColor = Color.Transparent,
                                        unfocusedTextColor = Color.White,
                                        focusedTextColor = Color.White
                                    ),
                                    placeholder = { Text("About", color = Color.Gray) },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = {
                                                saveProfileUpdates() // Save the "About" text
                                                isEditingAbout = false // Stop editing
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Save About",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                )
                            } else {
                                Text(
                                    text = if (aboutText.isNotEmpty()) aboutText else "Tap to add about info",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
