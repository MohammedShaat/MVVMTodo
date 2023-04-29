package com.codinginflow.mvvmtodo.ui.deleteallcompletedtasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.di.ApplicationCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllCompletedTasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @ApplicationCoroutine private val applicationCoroutine: CoroutineScope
) : ViewModel() {

    fun onYesClick() = applicationCoroutine.launch {
        taskDao.deleteAllCompletedTasks()
    }
}