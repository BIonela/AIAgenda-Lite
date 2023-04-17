package com.example.aiagenda.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.aiagenda.R
import com.example.aiagenda.databinding.FragmentCreateTaskBinding
import com.example.aiagenda.model.Task
import com.example.aiagenda.util.UiStatus
import com.example.aiagenda.viewmodel.AuthViewModel
import com.example.aiagenda.viewmodel.CreateTaskViewModel
import com.example.aiagenda.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class CreateTaskFragment : Fragment() {
    private lateinit var binding: FragmentCreateTaskBinding
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(requireActivity().application)
    }
    private val createTaskViewModel: CreateTaskViewModel by viewModels {
        ViewModelFactory(requireActivity().application)
    }
    private var uri = ""

    private var gallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (isAdded) {
            if (uri != null) {
                this.uri = uri.toString()
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.progress_animation)
                    .centerCrop()
                    .into(binding.ivTaskPhoto)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_create_task,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUiState()

        binding.viewModel = createTaskViewModel
        binding.lifecycleOwner = this

        binding.ivArrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.etStartDate.setOnClickListener {
            openDatePickerDialog(binding.etStartDate)
        }

        binding.etEndDate.setOnClickListener {
            openDatePickerDialog(binding.etEndDate)
        }

        binding.ivStartDate.setOnClickListener {
            openDatePickerDialog(binding.etStartDate)
        }

        binding.ivEndDate.setOnClickListener {
            openDatePickerDialog(binding.etEndDate)
        }

        binding.btnAddPhoto.setOnClickListener {
            gallery.launch("image/*")
        }

        binding.btnCreateTask.setOnClickListener {
            authViewModel.getSession { user ->
                if (user != null) {
                    val task = Task(
                        id = Random().nextLong().toString(),
                        title = binding.etTitle.text.toString(),
                        className = binding.etClass.text.toString(),
                        startDay = binding.etStartDate.text.toString(),
                        endDay = binding.etEndDate.text.toString(),
                        description = binding.etDescription.text.toString()
                    )
                    createTaskViewModel.addTask(user, task, uri)
                }
            }
        }
    }

    private fun observeUiState() {
        createTaskViewModel.uiState.observe(viewLifecycleOwner) { uiStatus ->
            when (uiStatus) {
                UiStatus.LOADING -> {
                    binding.apply {
                        pbLoading.visibility = View.VISIBLE
                        btnAddPhoto.visibility = View.GONE
                        btnCreateTask.visibility = View.GONE
                    }
                }
                UiStatus.SUCCESS -> {
                    binding.apply {
                        clearFields()
                        pbLoading.visibility = View.GONE
                        btnAddPhoto.visibility = View.VISIBLE
                        btnCreateTask.visibility = View.VISIBLE
                    }
                }
                else -> {
                    findNavController().navigate(
                        CreateTaskFragmentDirections.actionCreateTaskFragmentToDialogFragment(
                            getString(R.string.another_exception),
                            false,
                            true
                        )
                    )
                }
            }
        }
    }

    private fun clearFields() {
        binding.apply {
            etTitle.text.clear()
            etClass.text.clear()
            etStartDate.text.clear()
            etEndDate.text.clear()
            etDescription.text.clear()
            ivTaskPhoto.setImageDrawable(null)
        }
        this.uri = ""
    }

    private fun openDatePickerDialog(editTextField: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, yearPicked, monthOfYear, dayOfMonth ->
                val datePicked = String.format(
                    "%02d/%02d/%02d", dayOfMonth, monthOfYear + 1, yearPicked
                )
                editTextField.setText(datePicked)
            }, year, month, day
        )

        val stringDate = binding.etStartDate.text.toString()
        if (stringDate.isNotBlank()) {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val date = formatter.parse(stringDate)
            val startDateCalendar = Calendar.getInstance()
            if (date != null) {
                startDateCalendar.time = date
            }
            datePickerDialog.datePicker.minDate = startDateCalendar.timeInMillis
        } else {
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
        }
        datePickerDialog.show()
    }

}