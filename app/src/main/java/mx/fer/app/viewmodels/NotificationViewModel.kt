package mx.fer.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.fer.app.model.AppRepository
import mx.fer.app.model.Notification
import mx.fer.app.model.NotificationGroup
import mx.fer.app.model.UserPreferencesRepository
import mx.fer.app.model.groupByRecency


data class NotificationsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,     // pull-to-refresh: keeps the list visible
    val notifications: List<Notification> = emptyList(),
    val readIds: Set<String> = emptySet(),
    val error: String? = null
) {
    val unreadCount: Int get() = notifications.count { it.id !in readIds }
    val groups: List<Pair<NotificationGroup, List<Notification>>> get() = notifications.groupByRecency()

    fun isRead(notification: Notification) = notification.id in readIds
}

/**
 * Shared between the bell (unread badge) and the notifications screen,
 * so it is created once in MainContent.
 */
class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository()
    private val preferences = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.readNotificationIds.collect { ids ->
                _uiState.update { it.copy(readIds = ids) }
            }
        }
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getNotifications()
                .onSuccess { list ->
                    _uiState.update { it.copy(isLoading = false, notifications = list) }
                    // Only after a successful load: a failed one must not wipe the read state
                    preferences.keepReadNotificationsOnly(list.map { it.id }.toSet())
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    // Pull to refresh, also called when coming back to the app so panel edits show up
    fun refresh() {
        if (_uiState.value.isLoading || _uiState.value.isRefreshing) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            repository.getNotifications()
                .onSuccess { list ->
                    _uiState.update { it.copy(isRefreshing = false, notifications = list, error = null) }
                    preferences.keepReadNotificationsOnly(list.map { it.id }.toSet())
                }
                .onFailure {
                    // Keep the current list; the user can pull again
                    _uiState.update { it.copy(isRefreshing = false) }
                }
        }
    }

    // Used when the notice is opened from a push
    fun markAsRead(notificationId: String) {
        viewModelScope.launch { preferences.markNotificationsRead(listOf(notificationId)) }
    }

    fun markAsRead(notification: Notification) {
        if (_uiState.value.isRead(notification)) return
        viewModelScope.launch { preferences.markNotificationsRead(listOf(notification.id)) }
    }
}
