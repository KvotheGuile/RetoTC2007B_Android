package mx.fer.app.model

import android.content.Context
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
        val USER_TYPE = stringPreferencesKey("user_type")
    }

    // Emits null while the user hasn't completed the initial setup
    val preferences: Flow<UserPreferences?> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { prefs ->
            val name = prefs[Keys.NAME] ?: return@map null
            val type = prefs[Keys.USER_TYPE]
                ?.let { runCatching { UserType.valueOf(it) }.getOrNull() }
                ?: return@map null
            UserPreferences(name, type)
        }

    suspend fun save(name: String, userType: UserType) {
        context.dataStore.edit { prefs ->
            prefs[Keys.NAME] = name
            prefs[Keys.USER_TYPE] = userType.name   // stored as "BECARIO" / "ASPIRANTE"
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}