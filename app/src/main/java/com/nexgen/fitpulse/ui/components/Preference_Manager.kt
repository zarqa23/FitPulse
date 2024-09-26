package com.nexgen.fitpulse.ui.components
import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private const val PREFS_NAME = "profile_prefs"
    private const val LAST_UNLOCKED_DAY = "last_unlocked_day"
    private const val PREFERENCES_NAME = "fit_pulse_prefs"
    private const val FIRST_NAME_KEY = "first_name"
    private const val LAST_NAME_KEY = "last_name"
    private const val EMAIL_KEY = "email"
    private const val KEY_PROFILE_IMAGE_URI = "profile_image_uri"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserDetails(context: Context, firstName: String, lastName: String, email: String) {
        val editor = getPreferences(context).edit()
        editor.putString(FIRST_NAME_KEY, firstName)
        editor.putString(LAST_NAME_KEY, lastName)
        editor.putString(EMAIL_KEY, email)
        editor.apply()
    }

    fun getFirstName(context: Context): String? {
        return getPreferences(context).getString(FIRST_NAME_KEY, "First Name")
    }


    fun getLastName(context: Context): String? {
        return getPreferences(context).getString(LAST_NAME_KEY, "Last Name")
    }

    fun getEmail(context: Context): String? {
        return getPreferences(context).getString(EMAIL_KEY, "")
    }

    fun saveProfileImageUri(context: Context, uri: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_PROFILE_IMAGE_URI, uri).apply()
    }

    fun getProfileImageUri(context: Context): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_PROFILE_IMAGE_URI, null)
    }

    fun getLastUnlockedDay(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(LAST_UNLOCKED_DAY, 1) // Default to Day 1 being unlocked
    }

    fun setLastUnlockedDay(context: Context, day: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(LAST_UNLOCKED_DAY, day).apply()
    }
}
