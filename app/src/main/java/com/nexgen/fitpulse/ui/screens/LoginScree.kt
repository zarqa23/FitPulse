package com.nexgen.fitpulse.ui.screens

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.nexgen.fitpulse.MainActivity
import com.nexgen.fitpulse.R
import com.nexgen.fitpulse.ui.viewmodel.UserSelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController , viewModel: UserSelectionViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    var user by remember { mutableStateOf<FirebaseUser?>(null) }
    val firstName = sharedPreferences.getString("first_name", "John") // Default first name
    val lastName = sharedPreferences.getString("last_name", "Doe") // Default last name
    val isFormValid = email.isNotEmpty() && password.isNotEmpty()
    val editor = sharedPreferences.edit()
    val scope = rememberCoroutineScope()
    // State for the phone number dialog
    val googleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_cliet_id))
            .requestEmail()
            .build()
    )
    // Observe authentication state
    val auth = FirebaseAuth.getInstance()
    LaunchedEffect(auth.currentUser) {
    }

    fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        (context as Activity).startActivityForResult(signInIntent, MainActivity.RC_SIGN_IN)
    }

    var isPhoneDialogOpen by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    // Initialize Google Sign-In options

    // Launch Google Sign-In Intent
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!, context, navController)
        } catch (e: ApiException) {
            Toast.makeText(context, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    Box(
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
    ) {
        // Skip text at top-right
        Text(
            text = "Skip",
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(16.dp)
                .padding(end = 12.dp, top = 32.dp)
                .align(Alignment.TopEnd)
                .zIndex(1f)
                .clickable {
                    viewModel.setSkipLoginState(true)
                    navController.navigate("home")
                }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(42.dp))

            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontSize = 26.sp,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Enter your email and password to log in.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.LightGray,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email TextField
            LoginTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password TextField
            LoginTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                isPasswordVisible = isPasswordVisible,
                onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Login Button
            Button(
                onClick = {
                    if (isFormValid) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    viewModel.setLoginState(true)
                                    // Save email and password to SharedPreferences
                                    editor.putString("email", email)
                                    editor.putString("password", password)
                                    editor.apply()
                                    Toast.makeText(context, "Logged in successfully", Toast.LENGTH_SHORT).show()
                                    navController.navigate("home")
                                } else {
                                    Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD9D9D9) ,
                    contentColor = Color(0xFF025D93) ,
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Login",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Up Text
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Don't have an account?",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_regular))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sign Up",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate("registration") // Navigate to signup screen
                    }
                )
            }
            // Spacer for "OR" Section
            Spacer(modifier = Modifier.height(32.dp))

            // OR Section with two dividers
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(
                    color = Color.White,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "OR",
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Divider(
                    color = Color.White,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            // Add this below the "OR" section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                // Sign in with Google button
                Button(
                    onClick = {
                        signInWithGoogle()
                        if (auth.currentUser != null) {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        viewModel.setLoginState(true)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(BorderStroke(1.dp, Color.White), RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_icon), // Replace with your Google icon resource
                        contentDescription = "Google Icon",
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign in with Google",
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sign in with Phone button
                Button(
                    onClick = { isPhoneDialogOpen = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .border(BorderStroke(1.dp, Color.White), RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone, // Use default phone icon
                        contentDescription = "Phone Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign in with Phone",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        // Phone Number Dialog
        if (isPhoneDialogOpen) {
            AlertDialog(
                onDismissRequest = { isPhoneDialogOpen = false },
                title = {
                    Text(text = "Enter Phone Number",
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF025D93),)
                },
                text = {
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
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
                        onClick = {
                            // Navigate to home screen after phone number input
                            isPhoneDialogOpen = false
                            showNotification(context)
                            editor.putString("phone_number", phoneNumber)
                            editor.apply()
                            viewModel.setLoginState(true)
                            navController.navigate("home")
                        }
                    ) {
                        Text("Submit" , fontFamily = FontFamily(Font(R.font.roboto_regular)),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            isPhoneDialogOpen = false
                        }
                    ) {
                        Text("Cancel" , fontFamily = FontFamily(Font(R.font.roboto_regular)),
                            fontWeight = FontWeight.Bold,
                            color = Color.White)
                    }
                }
            )
        }
    }
}

fun firebaseAuthWithGoogle(idToken: String, context: Context, navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                Toast.makeText(context, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show()
                navController.navigate("home") // Navigate to HomeScreen
            } else {
                Toast.makeText(context, "Google Sign-In Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null
) {
    val visualTransformation = if (isPassword && !isPasswordVisible) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label , fontFamily = FontFamily(Font(R.font.poppins_regular))) },
        leadingIcon = {
            leadingIcon?.let {
                Icon(it, contentDescription = null, tint = Color.White)
            }
        },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { onTogglePasswordVisibility?.invoke() }) {
                    val eyeIcon: Painter = if (isPasswordVisible) {
                        painterResource(id = R.drawable.eye_open) // Your eye open image resource
                    } else {
                        painterResource(id = R.drawable.eye_close) // Your eye closed image resource
                    }
                    Image(
                        painter = eyeIcon,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        visualTransformation = visualTransformation,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.White,
            unfocusedTextColor = Color.White,
            unfocusedBorderColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White,
            cursorColor = Color.White,
            focusedTrailingIconColor = Color.White,
            focusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 0.dp)
    )
}





