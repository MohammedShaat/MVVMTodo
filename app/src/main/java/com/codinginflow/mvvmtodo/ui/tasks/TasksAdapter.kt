package com.codinginflow.mvvmtodo.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.databinding.ItemTaskBinding
import kotlinx.android.synthetic.main.item_task.view.*

class TasksAdapter : ListAdapter<Task, TasksAdapter.TaskViewHolder>(TaskCallBack()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TaskViewHolder private constructor(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup): TaskViewHolder {
                val binding = ItemTaskBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return TaskViewHolder(binding)
            }
        }

        fun bind(task: Task) {
            binding.apply {
                checkBoxCompleteTask.isChecked = task.completed
                textViewTaskName.text = task.name
                textViewTaskName.paint.isStrikeThruText = task.completed
                imageViewPriority.isVisible = task.important
            }
        }
    }

    class TaskCallBack : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}