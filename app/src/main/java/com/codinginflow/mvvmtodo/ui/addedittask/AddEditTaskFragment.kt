package com.codinginflow.mvvmtodo.ui.addedittask

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.databinding.FragmentAddEditTaskBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AddEditTaskFragment"

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {

    private val viewModel: AddEditTaskViewModel by viewModels()
    private lateinit var binding: FragmentAddEditTaskBinding

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
        }
    }
}