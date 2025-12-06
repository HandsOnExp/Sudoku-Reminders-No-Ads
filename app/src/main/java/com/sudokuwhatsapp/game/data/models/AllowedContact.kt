package com.sudokuwhatsapp.game.data.models

/**
 * Represents an allowed WhatsApp contact or group
 * Messages from these contacts will trigger notifications in the game
 */
data class AllowedContact(
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = "",  // For WhatsApp contact matching
    val isGroup: Boolean = false,
    val isEnabled: Boolean = true
)
