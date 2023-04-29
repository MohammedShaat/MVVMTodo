package com.codinginflow.mvvmtodo.ui.deleteallcompletedtasks

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCompletedTasks : DialogFragment() {

    private val viewModel: DeleteAllCompletedTasksViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Deletion Confirmation")
            .setMessage("Are you sure you want to delete all completed tasks?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.onYesClick()
            }
            .create()
    }
}