package com.example.aiagenda.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aiagenda.util.UiStatus

class UiStateViewModel : ViewModel() {

    private val _uiState = MutableLiveData<UiStatus>()
    val uiState: LiveData<UiStatus> = _uiState

    fun setLoading() {
        _uiState.postValue(UiStatus.LOADING)
    }

    fun setSuccess() {
        _uiState.postValue(UiStatus.SUCCESS)
    }

    fun setError() {
        _uiState.postValue(UiStatus.ERROR)
    }


}