package mx.fer.app.model

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val NAME = stringPreferencesKey("name")
        val PUSH = booleanPreferencesKey("push_notifications_enabled")
        val SCHOLARSHIP = booleanPreferencesKey("interested_scholarship")
        val APPLICANTS = booleanPreferencesKey("interested_applicants")
        val GENERAL = booleanPreferencesKey("interested_general")
    }

    // Emits null until the user has completed the initial setup (no name saved yet)
    val preferences: Flow<UserPreferences?> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { prefs ->
            val name = prefs[Keys.NAME] ?: return@map null
            UserPreferences(
                name = name,
                pushNotificationsEnabled = prefs[Keys.PUSH] ?: true,
                interestedScholarship = prefs[Keys.SCHOLARSHIP] ?: true,
                interestedApplicants = prefs[Keys.APPLICANTS] ?: true,
                interestedGeneral = prefs[Keys.GENERAL] ?: true
            )
        }

    suspend fun save(preferences: UserPreferences) {
        context.dataStore.edit { prefs ->
            prefs[Keys.NAME] = preferences.name
            prefs[Keys.PUSH] = preferences.pushNotificationsEnabled
            prefs[Keys.SCHOLARSHIP] = preferences.interestedScholarship
            prefs[Keys.APPLICANTS] = preferences.interestedApplicants
            prefs[Keys.GENERAL] = preferences.interestedGeneral
        }
    }

    suspend fun setPushNotificationsEnabled(enabled: Boolean) = setBoolean(Keys.PUSH, enabled)
    suspend fun setInterestedScholarship(enabled: Boolean) = setBoolean(Keys.SCHOLARSHIP, enabled)
    suspend fun setInterestedApplicants(enabled: Boolean) = setBoolean(Keys.APPLICANTS, enabled)
    suspend fun setInterestedGeneral(enabled: Boolean) = setBoolean(Keys.GENERAL, enabled)

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    private suspend fun setBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        context.dataStore.edit { it[key] = value }
    }
}