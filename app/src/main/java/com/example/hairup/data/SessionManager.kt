package com.example.hairup.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.hairup.model.User

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("hairup_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_XP = "user_xp"
        private const val KEY_USER_POINTS = "user_points"
        private const val KEY_USER_LEVEL = "user_level"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_IS_ADMIN = "is_admin"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveAuthData(token: String, user: User) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putInt(KEY_USER_ID, user.id)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_NAME, user.name)
            putInt(KEY_USER_XP, user.xp)
            putInt(KEY_USER_POINTS, user.points)
            putInt(KEY_USER_LEVEL, user.levelId)
            putString(KEY_USER_PHONE, user.phone)
            putBoolean(KEY_IS_ADMIN, user.isAdmin)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun isAdmin(): Boolean = prefs.getBoolean(KEY_IS_ADMIN, false)

    fun getUser(): User? {
        if (!isLoggedIn()) return null
        return User(
            id = prefs.getInt(KEY_USER_ID, 0),
            email = prefs.getString(KEY_USER_EMAIL, "") ?: "",
            name = prefs.getString(KEY_USER_NAME, "") ?: "",
            password = "",
            xp = prefs.getInt(KEY_USER_XP, 0),
            points = prefs.getInt(KEY_USER_POINTS, 0),
            levelId = prefs.getInt(KEY_USER_LEVEL, 1),
            phone = prefs.getString(KEY_USER_PHONE, "") ?: "",
            isAdmin = prefs.getBoolean(KEY_IS_ADMIN, false)
        )
    }

    fun logout() {
        prefs.edit { clear() }
    }
}