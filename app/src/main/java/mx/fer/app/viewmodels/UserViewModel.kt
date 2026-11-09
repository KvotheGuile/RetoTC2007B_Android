package mx.fer.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.fer.app.model.ThemeMode
import mx.fer.app.model.UserPreferences
import mx.fer.app.model.UserPreferencesRepository
import mx.fer.app.model.UserType
import mx.fer.app.model.toDefaultPreferences

data class UserUiState(
    val isLoading: Boolean = true,
    val preferences: UserPreferences? = null   // null = show login
)

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserPreferencesRepository(application)

    val uiState: StateFlow<UserUiState> = repository.preferences
        .map { UserUiState(isLoading = false, preferences = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserUiState()
        )

    // Called by the login screen: saves the name plus the defaults for the chosen type
    fun completeSetup(name: String, userType: UserType) {
        viewModelScope.launch {
            repository.save(userType.toDefaultPreferences(name.trim()))
        }
    }

    fun setPushNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setPushNotificationsEnabled(enabled) }
    }

    fun setInterestedScholarship(enabled: Boolean) {
        viewModelScope.launch { repository.setInterestedScholarship(enabled) }
    }

    fun setInterestedApplicants(enabled: Boolean) {
        viewModelScope.launch { repository.setInterestedApplicants(enabled) }
    }

    fun setInterestedGeneral(enabled: Boolean) {
        viewModelScope.launch { repository.setInterestedGeneral(enabled) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    fun clear() {
        viewModelScope.launch { repository.clear() }
    }
}