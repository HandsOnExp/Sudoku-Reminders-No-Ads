package com.sudokuwhatsapp.game.reminders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sudokuwhatsapp.game.data.models.CustomReminder
import com.sudokuwhatsapp.game.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Manages reminder timers and notifications during gameplay
 */
class ReminderManager(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application.applicationContext)
    private val scope = CoroutineScope(Dispatchers.Main)

    private val reminderJobs = mutableMapOf<String, Job>()

    private val _reminderQueue = MutableStateFlow<List<CustomReminder>>(emptyList())
    val reminderQueue: StateFlow<List<CustomReminder>> = _reminderQueue.asStateFlow()

    // Derived state for current reminder (first in queue)
    val currentReminder: StateFlow<CustomReminder?> = _reminderQueue
        .map { it.firstOrNull() }
        .stateIn(scope, SharingStarted.Eagerly, null)

    /**
     * Add reminder to queue with duplicate detection
     * Prevents the same reminder from being queued multiple times
     */
    private fun addReminderToQueue(reminder: CustomReminder) {
        _reminderQueue.update { currentQueue ->
            // Check if this reminder is already in the queue
            val alreadyQueued = currentQueue.any { it.id == reminder.id }

            if (!alreadyQueued) {
                currentQueue + reminder  // Add to end of queue
            } else {
                currentQueue  // Don't add duplicates
            }
        }
    }

    /**
     * Start all enabled reminder timers
     */
    fun startReminders() {
        stopAllReminders()

        val reminders = repository.loadReminders().filter { it.isEnabled }

        reminders.forEach { reminder ->
            val job = scope.launch {
                // Wait for the interval before showing the first reminder
                delay(reminder.intervalMinutes * 60 * 1000L)

                while (true) {
                    // Add reminder to queue
                    addReminderToQueue(reminder)

                    // Wait for interval before next reminder
                    delay(reminder.intervalMinutes * 60 * 1000L)
                }
            }
            reminderJobs[reminder.id] = job
        }
    }

    /**
     * Stop all reminder timers
     */
    fun stopAllReminders() {
        reminderJobs.values.forEach { it.cancel() }
        reminderJobs.clear()
        _reminderQueue.value = emptyList()
    }

    /**
     * Dismiss the current reminder
     */
    fun dismissReminder() {
        _reminderQueue.update { it.drop(1) }
    }

    override fun onCleared() {
        super.onCleared()
        stopAllReminders()
    }
}
