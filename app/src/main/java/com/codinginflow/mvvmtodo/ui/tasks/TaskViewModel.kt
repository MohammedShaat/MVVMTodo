package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.codinginflow.mvvmtodo.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val sortOrder = MutableStateFlow(SortOrder.BY_DATE_CREATED)
    val hideCompleted = MutableStateFlow(false)

    private val tasksFlow =
        combine(searchQuery, sortOrder, hideCompleted) { searchQuery, sortOrder, hideCompleted ->
            Triple(searchQuery, sortOrder, hideCompleted)
        }
            .flatMapLatest { (searchQuery, sortOrder, hideCompleted) ->
                taskDao.getTasks(searchQuery, sortOrder, hideCompleted)
            }

    val tasks = tasksFlow.asLiveData()
}

enum class SortOrder(column: String) {
    BY_NAME("name"), BY_DATE_CREATED("created")
}