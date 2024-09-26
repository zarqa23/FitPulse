package com.nexgen.fitpulse.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nexgen.fitpulse.R
import com.nexgen.fitpulse.ui.components.PreferenceManager
import com.nexgen.fitpulse.ui.viewmodel.UserSelectionViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    viewModel: UserSelectionViewModel
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val firstName = PreferenceManager.getFirstName(context) ?: "First Name"
    val lastName = PreferenceManager.getLastName(context) ?: "Last Name"
    val email = PreferenceManager.getEmail(context) ?: "Email"
    var profileImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var goal by remember { mutableStateOf(sharedPreferences.getString("dailyGoal", "Add Goal")) }
    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var newGoal by remember { mutableStateOf(goal ?: "") }
    var userName by remember { mutableStateOf(sharedPreferences.getString("userName", "")) }
    var isUserNameEditable by remember { mutableStateOf(userName.isNullOrEmpty()) }
    var isUserNameSaved by remember { mutableStateOf(false) }
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
        containerColor = Color(0xFF011C2D),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
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
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
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
                            painter = painterResource(id = R.drawable.profile_image_default),
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
                    // Edit Icon Button
                    IconButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, CircleShape)
                            .align(Alignment.BottomEnd)
                            .offset(x = 0.dp, y = 0.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.camera_icon),
                            contentDescription = "Edit Image",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Name",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    // Username Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        ),
                    ) {
                        Column(
                            modifier = Modifier
                        ) {
                            if (isUserNameEditable) {
                                OutlinedTextField(
                                    value = userName ?: "",
                                    onValueChange = { newValue -> userName = newValue },
                                    enabled = isUserNameEditable,
                                    label = { Text("Enter your name") },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Save",
                                            modifier = Modifier.clickable {
                                                if (!userName.isNullOrBlank()) {
                                                    editor.putString("userName", userName)
                                                        .apply()
                                                    isUserNameEditable = false
                                                    isUserNameSaved = true
                                                }
                                            }
                                        )
                                    },
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedTrailingIconColor = Color.Green,
                                        focusedBorderColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        cursorColor = Color.White,
                                        focusedLabelColor = Color.Transparent,
                                        unfocusedLabelColor = Color.White,
                                        unfocusedBorderColor = Color.Transparent,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(
                                    text = userName ?: "",
                                    color = Color.White,
                                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                    modifier = Modifier.clickable { isUserNameEditable = true }
                                        .padding(12.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Display Email in a Card
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .padding(16.dp)
                            .background(Color.Transparent)
                            .border(1.dp, Color.White, RoundedCornerShape(8.dp)),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF024974))
                    ) {
                        Text(
                            text = "$email",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.poppins_regular)),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                // Divider Below Today's Target
                Divider(
                    color = Color.White,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                        .padding(bottom = 8.dp)
                )

                // Today's Target Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Today's Target",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                    if (goal == "Add Goal") {
                        // Show + Add Goal Button
                        Button(
                            onClick = { showDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.padding(start = 2.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Goal")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Add Goal", fontSize = 12.sp)
                        }
                    } else {
                        // Show current goal with Edit option
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = goal ?: "No Goal Set",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                            IconButton(
                                onClick = {
                                    newGoal = goal ?: "" // Initialize input with current goal
                                    showEditDialog = true
                                },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit Goal",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                // Divider Below Today's Target
                Divider(
                    color = Color.White,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                        .padding(top = 8.dp)
                )

                // Add Goal Dialog
                if (showDialog) {
                    var input by remember { mutableStateOf("") }
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Set Daily Goal" , color = Color(0xFF025D93)) },
                        text = {
                            OutlinedTextField(
                                value = input,
                                onValueChange = { input = it },
                                label = { Text("Enter your target (e.g., 200 cal)") },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF025D93),
                                    unfocusedTextColor = Color(0xFF025D93),
                                    unfocusedBorderColor = Color(0xFF025D93),
                                    focusedLabelColor = Color(0xFF025D93),
                                    unfocusedLabelColor = Color(0xFF025D93),
                                    cursorColor = Color(0xFF025D93),
                                    focusedTrailingIconColor = Color(0xFF025D93),
                                    focusedTextColor = Color.Black,
                                )
                            )
                        },
                        confirmButton = {
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                ),
                                onClick = {
                                    goal = input
                                    editor.putString("dailyGoal", input)
                                    editor.apply()
                                    showDialog = false
                                }
                            ) {
                                Text("Save", color = Color(0xFF025D93))
                            }
                        },
                        dismissButton = {
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent
                                ),
                                onClick = { showDialog = false }
                            ) {
                                Text("Cancel", color = Color(0xFF025D93))
                            }
                        }
                    )
                }

                // Edit Goal Dialog
                if (showEditDialog) {
                    AlertDialog(
                        onDismissRequest = { showEditDialog = false },
                        title = { Text("Edit Daily Goal" , color = Color(0xFF025D93)) },
                        text = {
                            OutlinedTextField(
                                value = newGoal,
                                onValueChange = { newGoal = it },
                                label = { Text("Update your target (e.g., 200 cal)") },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF025D93),
                                    unfocusedTextColor = Color(0xFF025D93),
                                    unfocusedBorderColor = Color(0xFF025D93),
                                    focusedLabelColor = Color(0xFF025D93),
                                    unfocusedLabelColor = Color(0xFF025D93),
                                    cursorColor = Color(0xFF025D93),
                                    focusedTrailingIconColor = Color(0xFF025D93),
                                    focusedTextColor = Color.Black,
                                )
                            )
                        },
                        confirmButton = {
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent
                                ),
                                onClick = {
                                    goal = newGoal
                                    editor.putString("dailyGoal", newGoal)
                                    editor.apply()
                                    showEditDialog = false
                                }
                            ) {
                                Text("Save", color = Color(0xFF025D93))
                            }
                        },
                        dismissButton = {
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent
                                ),
                                onClick = { showEditDialog = false }
                            ) {
                                Text("Cancel", color = Color(0xFF025D93))
                            }
                        }
                    )
                }
            }
        }
    }
}