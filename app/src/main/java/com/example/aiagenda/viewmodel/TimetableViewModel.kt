package com.example.aiagenda.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aiagenda.model.Course
import com.example.aiagenda.model.Timetable
import com.example.aiagenda.model.TimetableTime
import com.example.aiagenda.model.User
import com.example.aiagenda.repository.TimetableRepository
import com.example.aiagenda.util.UiStatus
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class TimetableViewModel(private val repository: TimetableRepository) : ViewModel() {

    val days = arrayOf("Luni", "Marti", "Miercuri", "Joi", "Vineri")

    private val _timetable: MutableLiveData<Timetable> =
        MutableLiveData<Timetable>()
    val timetable: LiveData<Timetable>
        get() = _timetable

    private val _groupCoursesByWeek: MutableLiveData<MutableList<Course>> =
        MutableLiveData<MutableList<Course>>()

    private val _groupCourses: MutableLiveData<MutableList<Course>> =
        MutableLiveData<MutableList<Course>>()

    private val _timetableTime: MutableLiveData<Map<String, TimetableTime>> =
        MutableLiveData<Map<String, TimetableTime>>()

    private val _uiState: MutableLiveData<UiStatus> =
        MutableLiveData<UiStatus>()
    val uiState: LiveData<UiStatus>
        get() = _uiState

    private val _spinnerValue: MutableLiveData<String> =
        MutableLiveData<String>()

    val groupTime: MediatorLiveData<Pair<Map<String, TimetableTime>, String>> =
        MediatorLiveData<Pair<Map<String, TimetableTime>, String>>().apply {
            addSource(_timetableTime) {
                value = Pair(it, _spinnerValue.value.toString())
            }
            addSource(_spinnerValue) {
                value = Pair(_timetableTime.value.orEmpty(), it)
            }
        }

    val allCoursesByGroup: MediatorLiveData<Pair<List<Course>, List<Course>>> =
        MediatorLiveData<Pair<List<Course>, List<Course>>>().apply {
            addSource(_groupCoursesByWeek) { value = Pair(it, _groupCourses.value.orEmpty()) }
            addSource(_groupCourses) { value = Pair(_groupCoursesByWeek.value.orEmpty(), it) }
        }

    fun setValue(newValue: String) {
        if (newValue == "1") {
            _spinnerValue.postValue("One")
        } else {
            _spinnerValue.postValue("Two")
        }
    }

    fun getCourses(user: User) {
        _uiState.postValue(UiStatus.LOADING)
        repository.getCourses(user, { timetable ->
            _timetable.postValue(timetable)
        }, { uiStatus ->
            _uiState.postValue(uiStatus)
        })
    }

    fun getTimetableTime() {
        _uiState.postValue(UiStatus.LOADING)
        repository.getTimetableTime({ timetableTime ->
            _timetableTime.postValue(timetableTime)
        }, {
            _uiState.postValue(it)
        })
    }

    fun getGroupCoursesByWeek(user: User, groupName: String, isOdd: Boolean) {
        _uiState.postValue(UiStatus.LOADING)
        if (groupName == "One") {
            if (isOdd) {
                repository.getGroupCoursesByWeek(user, "One", "Odd", {
                    _groupCoursesByWeek.postValue(it)
                }, {
                    _uiState.postValue(it)
                })
            } else {
                repository.getGroupCoursesByWeek(user, "One", "Even", {
                    _groupCoursesByWeek.postValue(it)
                }, {
                    _uiState.postValue(it)
                })
            }
        }
        if (groupName == "Two") {
            if (isOdd) {
                repository.getGroupCoursesByWeek(user, "Two", "Odd", {
                    _groupCoursesByWeek.postValue(it)
                }, {
                    _uiState.postValue(it)
                })
            } else {
                repository.getGroupCoursesByWeek(user, "Two", "Even", {
                    _groupCoursesByWeek.postValue(it)
                }, {
                    _uiState.postValue(it)
                })
            }
        }
    }

    fun getGroupCourses(user: User, groupName: String) {
        _uiState.postValue(UiStatus.LOADING)
        if (groupName == "One") {
            repository.getGroupCourses(user, "One", {
                _groupCourses.postValue(it)
            }, {
                _uiState.postValue(it)
            })
        }
        if (groupName == "Two") {
            repository.getGroupCourses(user, "Two", {
                _groupCourses.postValue(it)
            }, {
                _uiState.postValue(it)
            })
        }
    }

    fun isOdd(
        startYear: TimetableTime,
        startHoliday: TimetableTime,
        endHoliday: TimetableTime,
        result: (Boolean) -> Unit
    ) {
        val startDate = LocalDate.of(startYear.year, startYear.month, startYear.dayOfMonth)
        val currentDate = LocalDate.now()

        val holidayDateStart =
            LocalDate.of(startHoliday.year, startHoliday.month, startHoliday.dayOfMonth)
        val holidayDateEnd =
            LocalDate.of(endHoliday.year, endHoliday.month, endHoliday.dayOfMonth)
        val holidayWeeks = ChronoUnit.WEEKS.between(holidayDateStart, holidayDateEnd) + 1

        val weeksBetween = ChronoUnit.WEEKS.between(startDate, currentDate)
        var currentWeek = weeksBetween + 1

        if (currentDate >= holidayDateStart) {
            currentWeek -= holidayWeeks
        }

        if (currentWeek.toInt() % 2 == 0) {
            result.invoke(false)
        } else {
            result.invoke(true)
        }

        Log.e("currentWeek", currentWeek.toString())
    }
}