package com.nexgen.fitpulse

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nexgen.fitpulse.Navigation.NavigationGraph
import com.nexgen.fitpulse.ui.screens.LoginScreen
import com.nexgen.fitpulse.ui.screens.RegistrationScreen
import com.nexgen.fitpulse.ui.screens.SplashScreen
import com.nexgen.fitpulse.ui.screens.YourExperienceScreen
import com.nexgen.fitpulse.ui.screens.YourGoalsScreen
import com.nexgen.fitpulse.ui.screens.YourTargetAreaScreen
import com.nexgen.fitpulse.ui.theme.FitPulseTheme
import com.nexgen.fitpulse.ui.viewmodel.UserSelectionViewModel

class MainActivity : ComponentActivity() {
    companion object {
        const val RC_SIGN_IN = 9001
    }
    private lateinit var auth: FirebaseAuth
    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_cliet_id))
            .requestEmail()
            .build())
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Notification permission granted")
            } else {
                Log.d("MainActivity", "Notification permission denied")
            }
        }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("MainActivity", "Notification permission already granted")
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
    private var isToastShown = false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                // Google Sign-In was successful, authenticate with Firebase
                firebaseAuthWithGoogle(account.idToken!!)
                // Show the toast only if it hasn't been shown yet
                if (!isToastShown) {
                    Toast.makeText(this, "Login Successful, click again to continue", Toast.LENGTH_SHORT).show()
                    isToastShown = true // Update the flag so the toast is not shown again
                }
            } catch (e: ApiException) {
                // Google Sign-In failed, update UI appropriately
                Log.w("GoogleSignIn", "Google sign in failed", e)
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    val user = auth.currentUser

                    Log.d("GoogleSignIn", "signInWithCredential:success")
                    // Navigate to home screen
                        //startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("GoogleSignIn", "signInWithCredential:failure", task.exception)
                }
            }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkNotificationPermission()
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        AdManager.loadAd(this)
        MobileAds.initialize(this) {}
        auth = FirebaseAuth.getInstance() // Initialize Firebase Auth
        setContent {
            FitPulseTheme {
                NavigationGraph()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Optionally reload or prepare the ad again if needed
        AdManager.loadAd(this)
    }
}
