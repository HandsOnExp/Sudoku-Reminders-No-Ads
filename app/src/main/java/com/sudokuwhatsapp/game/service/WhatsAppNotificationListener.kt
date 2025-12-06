package com.sudokuwhatsapp.game.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

/**
 * Notification listener service for WhatsApp messages
 * Listens to incoming WhatsApp notifications and extracts message data
 */
class WhatsAppNotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "WhatsAppListener"
        private const val WHATSAPP_PACKAGE = "com.whatsapp"
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        // Only process WhatsApp notifications
        if (sbn.packageName != WHATSAPP_PACKAGE) {
            return
        }

        // Extract notification data
        val notification = sbn.notification
        val extras = notification.extras

        val title = extras.getString("android.title") ?: "Unknown"
        val text = extras.getCharSequence("android.text")?.toString() ?: "No message"

        // Log the notification details
        Log.d(TAG, "WhatsApp Notification Received:")
        Log.d(TAG, "From: $title")
        Log.d(TAG, "Message: $text")
        Log.d(TAG, "Timestamp: ${sbn.postTime}")
        Log.d(TAG, "---")

        // TODO: In future phases, we'll process this notification to trigger Sudoku challenges
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)

        if (sbn.packageName == WHATSAPP_PACKAGE) {
            Log.d(TAG, "WhatsApp notification removed")
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "WhatsApp Notification Listener Connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "WhatsApp Notification Listener Disconnected")
    }
}
