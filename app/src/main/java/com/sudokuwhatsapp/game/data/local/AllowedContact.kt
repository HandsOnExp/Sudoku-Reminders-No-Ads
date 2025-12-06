package com.sudokuwhatsapp.game.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity representing an allowed contact for WhatsApp notification filtering
 *
 * @param id Unique identifier for the database (auto-generated)
 * @param displayName Name shown in UI (e.g., "אבא", "אמא", "קבוצת תפילה")
 * @param identifier What to match in notifications (phone number, contact name, or group name)
 * @param addedDate Timestamp when this contact was added
 */
@Entity(
    tableName = "allowed_contacts",
    indices = [Index(value = ["identifier"], unique = true)]
)
data class AllowedContact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val displayName: String,
    val identifier: String,
    val addedDate: Long = System.currentTimeMillis()
)
