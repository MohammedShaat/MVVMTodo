package com.codinginflow.mvvmtodo.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import com.codinginflow.mvvmtodo.di.ApplicationCoroutine
import com.codinginflow.mvvmtodo.ui.tasks.SortOrder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private object PreferencesKeys {
        val sortOrder = preferencesKey<String>("sort_order")
        val hideCompleted = preferencesKey<Boolean>("hide_completed")
    }

    private val dataStore = context.createDataStore("user_preferences")

    val preferencesFilterFlow = dataStore.data
        .catch { e ->
            if (e is IOException) {
                Log.e(TAG, "Error reading preferences", e )
                emit(emptyPreferences())
            } else
            {
                throw e
            }
        }
        .map { preferences ->
        val sortOrder = SortOrder.valueOf(
            preferences[PreferencesKeys.sortOrder] ?: SortOrder.BY_DATE_CREATED.name
        )
        val hideCompleted = preferences[PreferencesKeys.hideCompleted] ?: false

        PreferencesFilter(sortOrder, hideCompleted)
    }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.sortOrder] = sortOrder.name
        }
    }

    suspend fun updateHideCompleted(hideCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.hideCompleted] = hideCompleted
        }
    }
}

data class PreferencesFilter(val sortOrder: SortOrder, val hideCompleted: Boolean)