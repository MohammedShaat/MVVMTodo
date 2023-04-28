package com.codinginflow.mvvmtodo.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao

class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val task = state.getLiveData<Task>("task")

//    val taskName = state.getLiveData("taskName", task.value?.name ?: "")
//    val important = state.getLiveData("taskImportant", task.value?.important ?: "")
//    val dateCreated = state.getLiveData("dateCreated", task.value?.createdFormatted ?: "")
}