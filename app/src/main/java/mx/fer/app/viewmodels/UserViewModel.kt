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
import mx.fer.app.model.PostType
import mx.fer.app.model.UserPreferencesRepository
import mx.fer.app.model.preferencesFromOnboarding

data class UserUiState(
    val isLoading: Boolean = true,
    val preferences: UserPreferences? = null   // null = show onboarding
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

    // Called at the end of onboarding with the chosen interests (at least one)
    fun completeOnboarding(interests: Set<PostType>, pushNotificationsEnabled: Boolean) {
        require(interests.isNotEmpty()) { "At least one interest is required" }
        viewModelScope.launch {
            repository.save(preferencesFromOnboarding(interests, pushNotificationsEnabled))
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