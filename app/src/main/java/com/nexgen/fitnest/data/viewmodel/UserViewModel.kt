package com.nexgen.fitnest.data.viewmodel

import android.content.SharedPreferences
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.nexgen.fitnest.data.database.User
import com.nexgen.fitnest.data.database.UserDao
import kotlinx.coroutines.launch
import java.util.Calendar

class UserViewModel(private val userDao: UserDao) : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun signUpUser(email: String, password: String, onComplete: (Boolean) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration successful
                    onComplete(true)
                } else {
                    // Registration failed
                    onComplete(false)
                }
            }
    }

    fun loginUser(email: String, password: String, onComplete: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful
                    onComplete(true)
                } else {
                    // Login failed
                    onComplete(false)
                }
            }
    }

    private var startTime: Long = 0

    fun startTrackingTime() {
        startTime = SystemClock.elapsedRealtime()
    }

    fun stopTrackingTime(sharedPreferences: SharedPreferences) {
        val endTime = SystemClock.elapsedRealtime()
        val timeSpent = endTime - startTime

        // Save timeSpent in SharedPreferences
        val today = getTodayDate() // Implement this to get today's date as a string
        val previousData = sharedPreferences.getStringSet("dailyTimeSpent", emptySet()) ?: emptySet()
        val updatedData = previousData.toMutableSet().apply {
            add("$today,$timeSpent")
        }
        sharedPreferences.edit().putStringSet("dailyTimeSpent", updatedData).apply()
    }

    fun getDailyTimeSpent(sharedPreferences: SharedPreferences): Map<String, Long> {
        val data = sharedPreferences.getStringSet("dailyTimeSpent", emptySet()) ?: emptySet()
        return data.map {
            val (date, time) = it.split(",")
            date to time.toLong()
        }.toMap()
    }

    // Helper function to get today's date in YYYY-MM-DD format
    private fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
    }

    fun getUserPhoneNumber(onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val phoneNumber = userDao.getUserPhoneNumber()
            onResult(phoneNumber)
        }
    }

    fun loginUser(phoneNumber: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = userDao.loginUser(phoneNumber)
            onResult(user != null)
        }
    }

    fun signUpUser(phoneNumber: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val count = userDao.isPhoneNumberTaken(phoneNumber)
            if (count > 0) {
                onResult(false) // Phone number already taken
            } else {
                userDao.insertUser(User(phoneNumber = phoneNumber))
                onResult(true) // Sign-up successful
            }
        }
    }

    fun deleteUser(phoneNumber: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                userDao.deleteUser(phoneNumber)
                onResult(true) // Deletion successful
            } catch (e: Exception) {
                onResult(false) // Deletion failed
            }
        }
    }
}

