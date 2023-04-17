package com.example.aiagenda.viewmodel

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aiagenda.model.Task
import com.example.aiagenda.model.User
import com.example.aiagenda.repository.CreateTaskRepository
import com.example.aiagenda.util.UiStatus

class CreateTaskViewModel(private val repository: CreateTaskRepository) : ViewModel() {

    private var _taskTitle = MutableLiveData<String>()
    val taskTitle: LiveData<String>
        get() = _taskTitle

    private var _className = MutableLiveData<String>()
    val className: LiveData<String>
        get() = _className

    private var _startTime = MutableLiveData<String>()
    val startTime: LiveData<String>
        get() = _startTime

    private var _endTime = MutableLiveData<String>()
    val endTime: LiveData<String>
        get() = _endTime

    private var _description = MutableLiveData<String>()
    val description: LiveData<String>
        get() = _description

    private val _uiState: MutableLiveData<UiStatus> =
        MutableLiveData<UiStatus>()
    val uiState: LiveData<UiStatus>
        get() = _uiState

    private var _isEnabled: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(_taskTitle) { value = setIsEnabled() }
        addSource(_className) { value = setIsEnabled() }
        addSource(_startTime) { value = setIsEnabled() }
        addSource(_endTime) { value = setIsEnabled() }
        addSource(_description) { value = setIsEnabled() }
    }
    val isEnabled: LiveData<Boolean>
        get() = _isEnabled

    fun setTaskTitle(taskTitle: Editable) {
        _taskTitle.postValue(taskTitle.toString())
    }

    fun setClassName(className: Editable) {
        _className.postValue(className.toString())
    }

    fun setStartTime(startTime: Editable) {
        _startTime.postValue(startTime.toString())
    }

    fun setEndTime(endTime: Editable) {
        _endTime.postValue(endTime.toString())
    }

    fun setDescription(description: Editable) {
        _description.postValue(description.toString())
    }

    private fun isValid(field: String?): Boolean {
        return !field.isNullOrEmpty()
    }

    private fun setIsEnabled(): Boolean {
        return (isValid(_taskTitle.value) && isValid(_className.value) && isValid(_startTime.value)
                && isValid(_endTime.value) && isValid(_description.value))
    }

    fun addTask(user: User, task: Task, photoUri: String) {
        _uiState.postValue(UiStatus.LOADING)
        repository.addTask(user, task, photoUri) { state ->
            _uiState.postValue(state)
        }
    }

}