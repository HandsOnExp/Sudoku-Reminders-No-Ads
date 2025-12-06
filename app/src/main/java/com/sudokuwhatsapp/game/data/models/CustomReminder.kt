package com.sudokuwhatsapp.game.data.models

/**
 * Represents a custom reminder message that appears during gameplay
 */
data class CustomReminder(
    val id: String = "",
    val message: String = "",
    val intervalMinutes: Int = 30,  // How often to show the reminder
    val isEnabled: Boolean = true
)
