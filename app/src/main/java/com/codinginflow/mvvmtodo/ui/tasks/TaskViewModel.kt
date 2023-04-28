package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.data.PreferencesManager
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val preferencesFilterFlow = preferencesManager.preferencesFilterFlow

    private val tasksFlow =
        combine(searchQuery, preferencesFilterFlow) { searchQuery, preferencesFilterFlow ->
            Pair(searchQuery, preferencesFilterFlow)
        }
            .flatMapLatest { (searchQuery, preferencesFilterFlow) ->
                taskDao.getTasks(
                    searchQuery,
                    preferencesFilterFlow.sortOrder,
                    preferencesFilterFlow.hideCompleted
                )
            }

    val tasks = tasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) {
        viewModelScope.launch {
            preferencesManager.updateSortOrder(sortOrder)
        }
    }

    fun onHideCompletedClicked(hideCompleted: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateHideCompleted(hideCompleted)
        }
    }

    fun onTaskClick(task: Task) {
        TODO("Not yet implemented")
    }

    fun onCheckBoxTaskClick(task: Task, checked: Boolean) {
        viewModelScope.launch {
            taskDao.update(task.copy(completed = checked))
        }
    }
}

enum class SortOrder(column: String) {
    BY_NAME("name"), BY_DATE_CREATED("created")
}