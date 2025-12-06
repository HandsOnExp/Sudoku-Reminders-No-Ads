package com.sudokuwhatsapp.game.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a WhatsApp message that passed the contact filter
 *
 * @param id Unique identifier for the database (auto-generated)
 * @param senderName Name of the sender (contact or group name)
 * @param content The message text content
 * @param timestamp When the message was received (milliseconds since epoch)
 * @param isFromGroup True if the message is from a WhatsApp group
 * @param groupName Name of the group if isFromGroup is true, null otherwise
 * @param isRead True if the user has viewed this message in the app
 */
@Entity(tableName = "filtered_messages")
data class FilteredMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromGroup: Boolean = false,
    val groupName: String? = null,
    val isRead: Boolean = false
)
