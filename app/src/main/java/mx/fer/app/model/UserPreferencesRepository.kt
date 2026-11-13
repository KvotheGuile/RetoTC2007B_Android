package mx.fer.app.model

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_completed")
        val LEGACY_NAME = stringPreferencesKey("name")   // older installs: setup was marked by a saved name
        val PUSH = booleanPreferencesKey("push_notifications_enabled")
        val SCHOLARSHIP = booleanPreferencesKey("interested_scholarship")
        val APPLICANTS = booleanPreferencesKey("interested_applicants")
        val GENERAL = booleanPreferencesKey("interested_general")

        val THEME = stringPreferencesKey("theme_mode")
        val TEXT_SIZE = stringPreferencesKey("text_size")
        val REDUCE_MOTION = booleanPreferencesKey("reduce_motion")

        val READ_NOTIFICATIONS = stringSetPreferencesKey("read_notification_ids")
    }

    // Emits null until the user has finished onboarding
    val preferences: Flow<UserPreferences?> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { prefs ->
            val done = prefs[Keys.ONBOARDING_DONE] == true || prefs[Keys.LEGACY_NAME] != null
            if (!done) return@map null
            UserPreferences(
                pushNotificationsEnabled = prefs[Keys.PUSH] ?: true,
                interestedScholarship = prefs[Keys.SCHOLARSHIP] ?: true,
                interestedApplicants = prefs[Keys.APPLICANTS] ?: true,
                interestedGeneral = prefs[Keys.GENERAL] ?: true,
                themeMode = prefs[Keys.THEME]
                    ?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                    ?: ThemeMode.SYSTEM,
                textSize = prefs[Keys.TEXT_SIZE]
                    ?.let { runCatching { TextSize.valueOf(it) }.getOrNull() }
                    ?: TextSize.SYSTEM,
                reduceMotion = prefs[Keys.REDUCE_MOTION]
            )
        }

    // IDs of the notifications the user already opened. Stored apart from the preferences
    // so it works the same before and after onboarding.
    val readNotificationIds: Flow<Set<String>> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { prefs -> prefs[Keys.READ_NOTIFICATIONS] ?: emptySet() }

    suspend fun markNotificationsRead(ids: Collection<String>) {
        if (ids.isEmpty()) return
        context.dataStore.edit { prefs ->
            prefs[Keys.READ_NOTIFICATIONS] = (prefs[Keys.READ_NOTIFICATIONS] ?: emptySet()) + ids
        }
    }

    suspend fun save(preferences: UserPreferences) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_DONE] = true
            prefs.remove(Keys.LEGACY_NAME)
            prefs[Keys.PUSH] = preferences.pushNotificationsEnabled
            prefs[Keys.SCHOLARSHIP] = preferences.interestedScholarship
            prefs[Keys.APPLICANTS] = preferences.interestedApplicants
            prefs[Keys.GENERAL] = preferences.interestedGeneral
            prefs[Keys.THEME] = preferences.themeMode.name
            prefs[Keys.TEXT_SIZE] = preferences.textSize.name
            preferences.reduceMotion?.let { prefs[Keys.REDUCE_MOTION] = it }
        }
    }

    suspend fun setPushNotificationsEnabled(enabled: Boolean) = setBoolean(Keys.PUSH, enabled)
    suspend fun setInterestedScholarship(enabled: Boolean) = setBoolean(Keys.SCHOLARSHIP, enabled)
    suspend fun setInterestedApplicants(enabled: Boolean) = setBoolean(Keys.APPLICANTS, enabled)
    suspend fun setInterestedGeneral(enabled: Boolean) = setBoolean(Keys.GENERAL, enabled)

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME] = mode.name }
    }
    suspend fun setTextSize(size: TextSize) {
        context.dataStore.edit { it[Keys.TEXT_SIZE] = size.name }
    }
    suspend fun setReduceMotion(enabled: Boolean) = setBoolean(Keys.REDUCE_MOTION, enabled)

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    private suspend fun setBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        context.dataStore.edit { it[key] = value }
    }
}