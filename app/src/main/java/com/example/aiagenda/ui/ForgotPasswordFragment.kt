package com.example.aiagenda.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.aiagenda.R
import com.example.aiagenda.databinding.FragmentForgotPasswordBinding
import com.example.aiagenda.util.AuthenticationStatus
import com.example.aiagenda.util.ValidationError
import com.example.aiagenda.viewmodel.AuthViewModel
import com.example.aiagenda.viewmodel.ViewModelFactory

class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_forgot_password,
            container,
            false
        )

        sendEmailStatus()
        updateIfLoading()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivArrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSendRecovery.setOnClickListener {
            validateForm()
            viewModel.forgotPassword(email = binding.etEmail.text.toString())
        }

    }

    private fun updateIfLoading() {
        viewModel.forgotPasswordError.observe(viewLifecycleOwner) {
            if (it == ValidationError.LOADING) {
                binding.pbLoading.visibility = View.VISIBLE
            } else {
                binding.pbLoading.visibility = View.GONE
            }
        }
    }

    private fun sendEmailStatus() {
        viewModel.repository.forgotPasswordStatus.observe(this.viewLifecycleOwner) {
            binding.tieEmailLayout.isErrorEnabled = false
            binding.pbLoading.visibility = View.GONE
            when (it) {
                AuthenticationStatus.SUCCESS -> {
                    findNavController().navigate(
                        ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToDialogFragment(
                            getString(R.string.send_reset_password_email),
                            true,
                            false
                        )
                    )
                }
                AuthenticationStatus.EMAIL_NOT_FOUND -> {
                    findNavController().navigate(
                        ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToDialogFragment(
                            getString(R.string.email_not_found),
                            false,
                            false
                        )
                    )
                }
                AuthenticationStatus.NO_INTERNET_CONNECTION -> {
                    findNavController().navigate(
                        ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToDialogFragment(
                            getString(
                                R.string.no_internet
                            ),
                            false,
                            false
                        )
                    )
                }
                AuthenticationStatus.EMAIL_INVALID -> {
                    findNavController().navigate(
                        ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToDialogFragment(
                            getString(
                                R.string.email_invalid
                            ),
                            false,
                            false
                        )
                    )
                }
                AuthenticationStatus.ANOTHER_EXCEPTION -> {
                    findNavController().navigate(
                        ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToDialogFragment(
                            getString(
                                R.string.another_exception
                            ),
                            false,
                            true
                        )
                    )
                }
                AuthenticationStatus.ERROR -> {
                    findNavController().navigate(
                        ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToDialogFragment(
                            getString(
                                R.string.another_exception
                            ),
                            false,
                            true
                        )
                    )
                }
                else -> return@observe
            }
        }
    }

    private fun validateForm() {
        viewModel.forgotPasswordError.observe(viewLifecycleOwner) {
            if (viewModel.forgotPasswordError.value == ValidationError.EMAIL_IS_EMPTY) {
                binding.apply {
                    tieEmailLayout.isErrorEnabled = true
                    tieEmailLayout.error = getString(R.string.empty_field)
                }
            }
            if (viewModel.forgotPasswordError.value == ValidationError.EMAIL_NOT_VALID) {
                binding.apply {
                    tieEmailLayout.isErrorEnabled = true
                    tieEmailLayout.error = getString(R.string.email_not_valid)
                }
            }
        }
    }
}