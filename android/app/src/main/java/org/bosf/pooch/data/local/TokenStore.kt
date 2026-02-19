package org.bosf.pooch.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pooch_scan_prefs")

@Singleton
class TokenStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val SELECTED_DOG_KEY = stringPreferencesKey("selected_dog_id")
    }

    val token: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]
    }

    val selectedDogId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[SELECTED_DOG_KEY]
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }

    suspend fun saveSelectedDog(dogId: String) {
        context.dataStore.edit { prefs ->
            prefs[SELECTED_DOG_KEY] = dogId
        }
    }

    suspend fun clearSelectedDog() {
        context.dataStore.edit { prefs ->
            prefs.remove(SELECTED_DOG_KEY)
        }
    }
}
