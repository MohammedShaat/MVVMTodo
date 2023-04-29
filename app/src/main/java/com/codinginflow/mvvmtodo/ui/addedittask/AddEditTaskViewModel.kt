package com.codinginflow.mvvmtodo.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.ui.ADD_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val TAG = "AddEditTaskViewModel"

class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val task = state.getLiveData<Task>("task")
    val taskName = state.getLiveData("taskName", "")
    val taskImportant = state.getLiveData("taskImportant", false)

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    fun onFabAddEditTaskClick(taskName: String, important: Boolean) = viewModelScope.launch {
        if (taskName.isBlank()) {
            addEditTaskEventChannel.send(AddEditTaskEvent.ShowEmptyNameMessage)
            return@launch
        }

        when (task.value) {
            is Task -> {
                val updatedTask = task.value!!.copy(name = taskName, important = important)
                taskDao.update(updatedTask)
                addEditTaskEventChannel.send(
                    AddEditTaskEvent.NavigateBackAfterTaskIsSaved(EDIT_TASK_RESULT_OK)
                )
            }
            null -> {
                taskDao.insert(Task(name = taskName, important = important))
                addEditTaskEventChannel.send(
                    AddEditTaskEvent.NavigateBackAfterTaskIsSaved(ADD_TASK_RESULT_OK)
                )
            }
        }
    }

    sealed class AddEditTaskEvent {
        object ShowEmptyNameMessage : AddEditTaskEvent()
        data class NavigateBackAfterTaskIsSaved(val result: Int) : AddEditTaskEvent()
    }
}
