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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Manages reminder timers and notifications during gameplay
 */
class ReminderManager(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application.applicationContext)
    private val scope = CoroutineScope(Dispatchers.Main)

    private val reminderJobs = mutableMapOf<String, Job>()

    private val _currentReminder = MutableStateFlow<CustomReminder?>(null)
    val currentReminder: StateFlow<CustomReminder?> = _currentReminder.asStateFlow()

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
                    // Show reminder
                    _currentReminder.value = reminder

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
        _currentReminder.value = null
    }

    /**
     * Dismiss the current reminder
     */
    fun dismissReminder() {
        _currentReminder.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopAllReminders()
    }
}
