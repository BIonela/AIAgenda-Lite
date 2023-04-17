package com.example.aiagenda.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aiagenda.R
import com.example.aiagenda.databinding.ItemTaskBinding
import com.example.aiagenda.model.Task
import com.example.aiagenda.util.calculateDaysLeft
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class TaskAdapter : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback) {

    var onItemClick: ((Task) -> Unit)? = null
    var onDelete: ((Task) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_task, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.title == newItem.title &&
                    oldItem.className == newItem.className &&
                    oldItem.startDay == newItem.startDay &&
                    oldItem.endDay == newItem.endDay &&
                    oldItem.photoUrl == newItem.photoUrl
        }
    }

    inner class TaskViewHolder(
        private var binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                tvTitleTask.text = task.title
                tvClassName.text = task.className
                lProgressIndicator.progress =
                    calculateDaysLeft(task.startDay, task.endDay)

                itemView.setOnClickListener {
                    onItemClick?.invoke(task)
                }

                ivDelete.setOnClickListener {
                    onDelete?.invoke(task)
                }
            }
        }

    }

}
