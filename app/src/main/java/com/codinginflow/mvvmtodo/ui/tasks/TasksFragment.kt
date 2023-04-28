package com.codinginflow.mvvmtodo.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.databinding.FragmentTasksBinding
import com.codinginflow.mvvmtodo.util.exhaustive
import com.codinginflow.mvvmtodo.util.onQueryTextChange
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TasksAdapter.OnItemClickListener {

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var binding: FragmentTasksBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTasksBinding.bind(view)
        val tasksAdapter = TasksAdapter(this)

        binding.recyclerViewTasks.apply {
            adapter = tasksAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task = tasksAdapter.currentList[viewHolder.adapterPosition]
                viewModel.onTaskSwiped(task)
            }
        }).attachToRecyclerView(binding.recyclerViewTasks)

        viewModel.tasks.observe(viewLifecycleOwner) { listTasks ->
            tasksAdapter.submitList(listTasks)
        }

        binding.fabAddTask.setOnClickListener {
            viewModel.onFabAddTaskClick()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.taskEvent.collect { taskEvent ->
                when (taskEvent) {
                    is TaskEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(view, "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo") {
                                viewModel.onUndoDeleteClick(taskEvent.task)
                            }
                            .show()
                    }
                    is TaskEvent.NavigateToAddEditTaskFragment -> {
                        val action =
                            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(title = "New Task")
                        findNavController().navigate(action)
                    }
                    is TaskEvent.NavigateToAddEditTaskFragmentWithTaskArg -> {
                        val action =
                            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                                taskEvent.task,
                                "Edit Task"
                            )
                        findNavController().navigate(action)

                    }
                }.exhaustive
            }
        }


        setHasOptionsMenu(true)
    }

    override fun onItemClick(task: Task) {
        viewModel.onTaskClick(task)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewModel.onCheckBoxTaskClick(task, isChecked)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks, menu)

        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.onQueryTextChange {
            viewModel.searchQuery.value = it
        }

        val hideCompleted = menu.findItem(R.id.hide_completed_tasks)
        viewLifecycleOwner.lifecycleScope.launch {
            hideCompleted.isChecked = viewModel.preferencesFilterFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                viewModel.onHideCompletedClicked(item.isChecked)
                true
            }

            R.id.delete_all_tasks -> {
                true
            }

            R.id.sort_tasks_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }

            R.id.sort_tasks_by_date_created -> {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE_CREATED)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}