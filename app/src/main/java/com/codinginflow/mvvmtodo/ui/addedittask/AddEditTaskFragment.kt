package com.codinginflow.mvvmtodo.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.databinding.FragmentAddEditTaskBinding
import com.codinginflow.mvvmtodo.ui.ADD_EDIT_RESULT_KEY
import com.codinginflow.mvvmtodo.ui.ADD_EDIT_TASK_REQUEST_KEY
import com.codinginflow.mvvmtodo.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private const val TAG = "AddEditTaskFragment"

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {

    private val viewModel: AddEditTaskViewModel by viewModels()
    private lateinit var binding: FragmentAddEditTaskBinding


    @Suppress("IMPLICIT_CAST_TO_ANY")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            editTextTaskName.setText(viewModel.task.value?.name)
            checkBoxImportant.isChecked = viewModel.task.value?.important ?: false
            textViewCreateDate.isVisible = viewModel.task.value != null
            textViewCreateDate.text = getString(
                R.string.textView_text_createDate,
                viewModel.task.value?.createdFormatted
            )

            fabAddEditTask.setOnClickListener {
                viewModel.onFabAddEditTaskClick(
                    editTextTaskName.text.toString(),
                    checkBoxImportant.isChecked
                )
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect { addEditTaskEvent ->
                when (addEditTaskEvent) {
                    is AddEditTaskViewModel.AddEditTaskEvent.ShowEmptyNameMessage -> {
                        Snackbar.make(view, "Name cannot be empty", Snackbar.LENGTH_SHORT).show()
                    }

                    is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackAfterTaskIsSaved -> {
                        setFragmentResult(
                            ADD_EDIT_TASK_REQUEST_KEY,
                            bundleOf(ADD_EDIT_RESULT_KEY to addEditTaskEvent.result)
                        )
                        findNavController().navigateUp()
                    }
                }.exhaustive
            }
        }
    }
}