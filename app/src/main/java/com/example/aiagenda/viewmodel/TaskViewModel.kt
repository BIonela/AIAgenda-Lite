package com.example.aiagenda.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aiagenda.model.Task
import com.example.aiagenda.model.TaskBody
import com.example.aiagenda.model.Timetable
import com.example.aiagenda.model.User
import com.example.aiagenda.repository.TaskRepository
import com.example.aiagenda.util.UiStatus

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasks: MutableLiveData<TaskBody> =
        MutableLiveData<TaskBody>()
    val tasks: LiveData<TaskBody>
        get() = _tasks

    private val _uiState: MutableLiveData<UiStatus> =
        MutableLiveData<UiStatus>()
    val uiState: LiveData<UiStatus>
        get() = _uiState

    fun getTasks(user: User) {
        _uiState.postValue(UiStatus.LOADING)
        repository.getTasks(user, { tasks ->
            _tasks.postValue(tasks)
        }, { uiStatus ->
            _uiState.postValue(uiStatus)
        })
    }

    fun deleteTask(user: User, task: Task) {
        _uiState.postValue(UiStatus.LOADING)
        repository.deleteTask(user, task) { state ->
            _uiState.postValue(state)
        }
    }


}