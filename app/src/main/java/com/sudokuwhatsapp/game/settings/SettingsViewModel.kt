package com.sudokuwhatsapp.game.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sudokuwhatsapp.game.data.models.AllowedContact
import com.sudokuwhatsapp.game.data.models.CustomReminder
import com.sudokuwhatsapp.game.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * ViewModel for managing settings (contacts and reminders)
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application.applicationContext)

    private val _contacts = MutableStateFlow<List<AllowedContact>>(emptyList())
    val contacts: StateFlow<List<AllowedContact>> = _contacts.asStateFlow()

    private val _reminders = MutableStateFlow<List<CustomReminder>>(emptyList())
    val reminders: StateFlow<List<CustomReminder>> = _reminders.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _contacts.value = repository.loadContacts()
        _reminders.value = repository.loadReminders()
    }

    // Contact management
    fun addContact(name: String, phoneNumber: String, isGroup: Boolean) {
        val newContact = AllowedContact(
            id = UUID.randomUUID().toString(),
            name = name,
            phoneNumber = phoneNumber,
            isGroup = isGroup,
            isEnabled = true
        )
        _contacts.value = _contacts.value + newContact
        saveContacts()
    }

    fun updateContact(contact: AllowedContact) {
        _contacts.value = _contacts.value.map {
            if (it.id == contact.id) contact else it
        }
        saveContacts()
    }

    fun deleteContact(contact: AllowedContact) {
        _contacts.value = _contacts.value.filter { it.id != contact.id }
        saveContacts()
    }

    fun toggleContact(contact: AllowedContact) {
        updateContact(contact.copy(isEnabled = !contact.isEnabled))
    }

    // Reminder management
    fun addReminder(message: String, intervalMinutes: Int) {
        val newReminder = CustomReminder(
            id = UUID.randomUUID().toString(),
            message = message,
            intervalMinutes = intervalMinutes,
            isEnabled = true
        )
        _reminders.value = _reminders.value + newReminder
        saveReminders()
    }

    fun updateReminder(reminder: CustomReminder) {
        _reminders.value = _reminders.value.map {
            if (it.id == reminder.id) reminder else it
        }
        saveReminders()
    }

    fun deleteReminder(reminder: CustomReminder) {
        _reminders.value = _reminders.value.filter { it.id != reminder.id }
        saveReminders()
    }

    fun toggleReminder(reminder: CustomReminder) {
        updateReminder(reminder.copy(isEnabled = !reminder.isEnabled))
    }

    // Persistence
    private fun saveContacts() {
        repository.saveContacts(_contacts.value)
    }

    private fun saveReminders() {
        repository.saveReminders(_reminders.value)
    }
}
