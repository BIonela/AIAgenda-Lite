package com.example.aiagenda.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val id: String = " ",
    val title: String = " ",
    val className: String = " ",
    val startDay: String = " ",
    val endDay: String = " ",
    val description: String = " ",
    var photoUrl: String = " "
) : Parcelable

data class TaskBody(
    val tasks: MutableList<Task> = mutableListOf()
)