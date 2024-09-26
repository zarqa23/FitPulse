package com.nexgen.fitpulse.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel

class UserSelectionViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    private val _selectedExperience = mutableStateOf<String?>(null)
    val selectedExperience: State<String?> get() = _selectedExperience

    private val _selectedGoal = mutableStateOf<String?>(null)
    val selectedGoal: State<String?> get() = _selectedGoal

    private val _selectedTargetArea = mutableStateOf<String?>(null)
    val selectedTargetArea: State<String?> get() = _selectedTargetArea

    // Mutable state for first and last name
    private val _firstName = mutableStateOf<String?>(null)
    val firstName: State<String?> get() = _firstName

    private val _lastName = mutableStateOf<String?>(null)
    val lastName: State<String?> get() = _lastName

    private val _isLoggedIn = mutableStateOf(PreferencesUtil.getLoginState(context))
    val isLoggedIn: State<Boolean> = _isLoggedIn

    fun setLoginState(loggedIn: Boolean) {
        _isLoggedIn.value = loggedIn
        PreferencesUtil.saveLoginState(context, loggedIn)
    }

    private val _isSkipLoggedIn = mutableStateOf(PreferencesUtil.getskipState(context))
    val isSkipLoggedIn: State<Boolean> = _isSkipLoggedIn

    fun setSkipLoginState(skipped: Boolean) {
        Log.d("UserSelectionViewModel", "Setting skip state to: $skipped")
        _isSkipLoggedIn.value = skipped
        PreferencesUtil.saveskipState(context, skipped)
    }


    init {
        // Load saved values from SharedPreferences
        _firstName.value = sharedPreferences.getString("first_name", null)
        _lastName.value = sharedPreferences.getString("last_name", null)
        _selectedExperience.value = sharedPreferences.getString("experience", null)
        _selectedGoal.value = sharedPreferences.getString("goal", null)
        _selectedTargetArea.value = sharedPreferences.getString("target_area", null)
        _isSkipLoggedIn.value = PreferencesUtil.getskipState(context)
        Log.d("UserSelectionViewModel", "Initial skip state: ${_isSkipLoggedIn.value}")
    }

    // Function to save first name and last name
    fun saveFirstName(firstName: String) {
        _firstName.value = firstName
        sharedPreferences.edit().putString("first_name", firstName).apply()
    }

    fun saveLastName(lastName: String) {
        _lastName.value = lastName
        sharedPreferences.edit().putString("last_name", lastName).apply()
    }

    fun setSelectedExperience(experience: String) {
        _selectedExperience.value = experience
        sharedPreferences.edit().putString("experience", experience).apply()
    }

    fun setSelectedGoal(goal: String) {
        _selectedGoal.value = goal
        sharedPreferences.edit().putString("goal", goal).apply()
    }

    fun setSelectedTargetArea(targetArea: String) {
        _selectedTargetArea.value = targetArea
        sharedPreferences.edit().putString("target_area", targetArea).apply()
    }



}


object PreferencesUtil {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_IS_SKIP_LOGGED_IN = "is_skip_logged_in"
    private val context: Context? = null

    fun saveLoginState(context: Context, isLoggedIn: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun getLoginState(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun saveskipState(context: Context, skipped: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_SKIP_LOGGED_IN, skipped).apply()
    }

    fun getskipState(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_SKIP_LOGGED_IN, false)
    }
}

