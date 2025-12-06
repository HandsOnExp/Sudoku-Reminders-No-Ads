package com.sudokuwhatsapp.game.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sudokuwhatsapp.game.data.models.CustomReminder

/**
 * Repository for persisting reminders using SharedPreferences
 */
class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "sudoku_reminders_settings"
        private const val KEY_REMINDERS = "custom_reminders"
    }

    // Reminder operations
    fun saveReminders(reminders: List<CustomReminder>) {
        val json = gson.toJson(reminders)
        prefs.edit().putString(KEY_REMINDERS, json).apply()
    }

    fun loadReminders(): List<CustomReminder> {
        val json = prefs.getString(KEY_REMINDERS, null) ?: return emptyList()
        val type = object : TypeToken<List<CustomReminder>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Clear all settings
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
