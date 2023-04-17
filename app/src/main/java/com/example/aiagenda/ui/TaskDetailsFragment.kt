package com.example.aiagenda.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.aiagenda.R
import com.example.aiagenda.databinding.FragmentTaskDetailsBinding
import com.example.aiagenda.util.calculateDaysLeft

class TaskDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTaskDetailsBinding
    private val args: TaskDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_task_details,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvTaskTitle.text = args.task.title
            tvCourseName.text = args.task.className
            tvStartDate.text = args.task.startDay
            tvEndDate.text = args.task.endDay
            tvDescriptionContent.text = args.task.description
            pbTask.progress = calculateDaysLeft(args.task.startDay, args.task.endDay)
            Log.e(
                "pb", calculateDaysLeft(args.task.startDay, args.task.endDay).toString()
            )
            tvProgress.text = getString(
                R.string.task_percent,
                calculateDaysLeft(args.task.startDay, args.task.endDay).toString()
            )
        }

        binding.layoutFullImage.ivArrowBack.setOnClickListener {
            binding.layoutFullImage.layoutChild.visibility = View.GONE
        }

        if (args.task.photoUrl.isNotBlank()) {
            Glide.with(requireContext())
                .load(args.task.photoUrl)
                .centerInside()
                .into(binding.ivTaskPhoto)

            binding.ivTaskPhoto.setOnClickListener {
                binding.layoutFullImage.layoutChild.visibility = View.VISIBLE
            }

            Glide.with(requireContext())
                .load(args.task.photoUrl)
                .centerInside()
                .into(binding.layoutFullImage.ivTaskPhoto)
        }

    }

}