package com.example.aiagenda.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.aiagenda.R
import com.example.aiagenda.databinding.FragmentTimetableBinding
import com.example.aiagenda.util.UiStatus
import com.example.aiagenda.viewmodel.AuthViewModel
import com.example.aiagenda.viewmodel.TimetableViewModel
import com.example.aiagenda.viewmodel.ViewModelFactory
import com.islandparadise14.mintable.model.ScheduleEntity
import kotlin.collections.ArrayList

class TimetableFragment : Fragment() {
    private lateinit var binding: FragmentTimetableBinding
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(requireActivity().application)
    }
    private val timetableViewModel: TimetableViewModel by viewModels {
        ViewModelFactory(requireActivity().application)
    }
    private val scheduleList: ArrayList<ScheduleEntity> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_timetable,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.timetable.initTable(timetableViewModel.days)

        setSpinner()
        observeUiState()

        authViewModel.getSession { user ->
            if (user != null) {
                timetableViewModel.getCourses(user)
            }
        }

        timetableViewModel.getTimetableTime()

        addAllCourses()
        addGroupCourses()
        observeGroupCourses()

    }

    private fun setSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner_center,
            resources.getStringArray(R.array.grupa)
        )
        binding.spGroup.adapter = adapter

        binding.spGroup.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?, position: Int, id: Long
            ) {
                if (position >= 0) {
                    timetableViewModel.setValue(resources.getStringArray(R.array.grupa)[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun addAllCourses() {
        timetableViewModel.timetable.observe(viewLifecycleOwner) { timetable ->
            timetable.courses.map {
                scheduleList.add(
                    ScheduleEntity(
                        originId = it.id,
                        scheduleName = it.name,
                        roomInfo = it.roomInfo,
                        scheduleDay = it.scheduleDay,
                        startTime = it.startTime,
                        endTime = it.endTime,
                        backgroundColor = "#FDA950"
                    )
                )
            }
            binding.timetable.updateSchedules(scheduleList)
        }
    }

    private fun addGroupCourses() {
        timetableViewModel.allCoursesByGroup.observe(viewLifecycleOwner) { groupCourses ->
            binding.timetable.updateSchedules(
                scheduleList.plus(
                    groupCourses.first.plus(groupCourses.second).map {
                        ScheduleEntity(
                            originId = it.id,
                            scheduleName = it.name,
                            roomInfo = it.roomInfo,
                            scheduleDay = it.scheduleDay,
                            startTime = it.startTime,
                            endTime = it.endTime,
                            backgroundColor = getRandomColor()
                        )
                    } as ArrayList<ScheduleEntity>) as ArrayList<ScheduleEntity>)
        }
    }

    private fun observeGroupCourses() {
        timetableViewModel.groupTime.observe(viewLifecycleOwner) { groupTime ->
            val time = groupTime.first
            val startTime = time["startTime"]
            val startHoliday = time["startHoliday"]
            val endHoliday = time["endHoliday"]

            authViewModel.getSession { user ->
                if (user != null) {
                    timetableViewModel.getGroupCourses(user, groupTime.second)
                    if (startTime != null && startHoliday != null && endHoliday != null) {
                        timetableViewModel.isOdd(startTime, startHoliday, endHoliday) { isOdd ->
                            timetableViewModel.getGroupCoursesByWeek(user, groupTime.second, isOdd)
                        }
                    }
                }
            }
        }
    }

    private fun observeUiState() {
        timetableViewModel.uiState.observe(viewLifecycleOwner) { uiStatus ->
            when (uiStatus) {
                UiStatus.LOADING -> {
                    binding.pbLoading.visibility = View.VISIBLE
                }
                UiStatus.SUCCESS -> {
                    binding.pbLoading.visibility = View.GONE
                }
                else -> {
                    findNavController().navigate(
                        TimetableFragmentDirections.actionTimetableFragmentToDialogFragment(
                            getString(R.string.another_exception),
                            false,
                            true
                        )
                    )
                }
            }
        }
    }

    private fun getRandomColor(): String {
        return resources.getStringArray(R.array.colors).random()
    }
}