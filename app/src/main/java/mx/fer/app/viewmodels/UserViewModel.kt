package mx.fer.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.fer.app.model.UserPreferences
import mx.fer.app.model.UserPreferencesRepository
import mx.fer.app.model.UserType

data class UserUiState(
    val isLoading: Boolean = true,            // true until DataStore answers
    val preferences: UserPreferences? = null  // null = show login
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

    fun save(name: String, userType: UserType) {
        viewModelScope.launch { repository.save(name.trim(), userType) }
    }

    fun clear() {
        viewModelScope.launch { repository.clear() }
    }
}