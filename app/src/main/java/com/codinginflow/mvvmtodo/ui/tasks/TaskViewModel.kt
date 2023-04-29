package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.data.PreferencesManager
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.ui.ADD_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "")
    val preferencesFilterFlow = preferencesManager.preferencesFilterFlow

    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

    private val tasksFlow =
        combine(searchQuery.asFlow(), preferencesFilterFlow) { searchQuery, preferencesFilterFlow ->
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
        viewModelScope.launch {
            taskEventChannel.send(TaskEvent.NavigateToAddEditTaskFragmentWithTaskArg(task))
        }
    }

    fun onCheckBoxTaskClick(task: Task, checked: Boolean) {
        viewModelScope.launch {
            taskDao.update(task.copy(completed = checked))
        }
    }

    fun onTaskSwiped(task: Task) {
        viewModelScope.launch {
            taskDao.delete(task)
            taskEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
        }
    }

    fun onUndoDeleteClick(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
        }
    }

    fun onFabAddTaskClick() {
        viewModelScope.launch {
            taskEventChannel.send(TaskEvent.NavigateToAddEditTaskFragment)
        }
    }

    fun onAddEditTaskResult(result: Int) = viewModelScope.launch {
        when (result) {
            ADD_TASK_RESULT_OK -> taskEventChannel.send(
                TaskEvent.ShowTaskSavedAfterEditOrAddMessage(R.string.new_task_message)
            )
            EDIT_TASK_RESULT_OK -> taskEventChannel.send(
                TaskEvent.ShowTaskSavedAfterEditOrAddMessage(R.string.update_task_message)
            )
        }
    }
}

enum class SortOrder(column: String) {
    BY_NAME("name"), BY_DATE_CREATED("created")
}

sealed class TaskEvent {
    data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()
    data class NavigateToAddEditTaskFragmentWithTaskArg(val task: Task) : TaskEvent()
    object NavigateToAddEditTaskFragment : TaskEvent()
    data class ShowTaskSavedAfterEditOrAddMessage(val messageResId: Int) : TaskEvent()
}