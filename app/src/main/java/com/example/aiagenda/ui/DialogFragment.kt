package com.example.aiagenda.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aiagenda.R
import com.example.aiagenda.databinding.FragmentDialogBinding

class DialogFragment : DialogFragment() {
    private lateinit var binding: FragmentDialogBinding
    private val args: DialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_dialog,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false

        binding.tvTitle.text = args.title

        binding.tvOk.setOnClickListener {
            if (args.toLogin) {
                findNavController().navigate(DialogFragmentDirections.actionDialogFragmentToLoginFragment())
            } else if (args.exitApp) {
                requireActivity().finishAndRemoveTask()
            } else {
                dismiss()
            }
        }
    }

}