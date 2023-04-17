package com.example.aiagenda.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.aiagenda.R
import com.example.aiagenda.databinding.FragmentLoginBinding
import com.example.aiagenda.util.AuthenticationStatus
import com.example.aiagenda.util.ValidationError
import com.example.aiagenda.viewmodel.AuthViewModel
import com.example.aiagenda.viewmodel.ViewModelFactory

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )
        registerStatus()
        updateIfLoading()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity()
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
            )
        }

        binding.btnLogin.setOnClickListener {
            validateForm()
            viewModel.login(
                email = binding.tieEmail.text.toString(),
                password = binding.tiePassword.text.toString(),
            )
        }

        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            pbLoading.visibility = View.GONE
            btnLogin.isEnabled = true
            tieEmail.text?.clear()
            tiePassword.text?.clear()
        }
    }

    private fun updateIfLoading() {
        viewModel.loginError.observe(viewLifecycleOwner) {
            if (it == ValidationError.LOADING) {
                binding.pbLoading.visibility = View.VISIBLE
                binding.btnLogin.isEnabled = false
            } else {
                binding.pbLoading.visibility = View.GONE
                binding.btnLogin.isEnabled = true
            }
        }
    }

    private fun registerStatus() {
        viewModel.repository.loginStatus.observe(this.viewLifecycleOwner) {

            binding.apply {
                pbLoading.visibility = View.GONE
                btnLogin.isEnabled = true
                tieEmailLayout.isErrorEnabled = false
                tiePasswordLayout.isErrorEnabled = false
            }

            when (it) {
                AuthenticationStatus.SUCCESS -> {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                    )
                }
                AuthenticationStatus.EMAIL_NOT_FOUND -> {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToDialogFragment(
                            getString(R.string.email_not_found),
                            false,
                            false
                        )
                    )
                }
                AuthenticationStatus.WRONG_PASSWORD_OR_EMAIL_INVALID -> {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToDialogFragment(
                            getString(R.string.wrong_password),
                            false,
                            false
                        )
                    )
                }
                AuthenticationStatus.NO_INTERNET_CONNECTION -> {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToDialogFragment(
                            getString(
                                R.string.no_internet
                            ),
                            false,
                            false
                        )
                    )
                }
                AuthenticationStatus.TOO_MANY_REQUESTS -> {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToDialogFragment(
                            getString(
                                R.string.too_many_requests
                            ),
                            false,
                            false
                        )
                    )
                }
                AuthenticationStatus.ANOTHER_EXCEPTION -> {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToDialogFragment(
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
                        LoginFragmentDirections.actionLoginFragmentToDialogFragment(
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
        viewModel.loginError.observe(viewLifecycleOwner) {
            if (viewModel.loginError.value == ValidationError.EMAIL_IS_EMPTY) {
                binding.apply {
                    tieEmailLayout.isErrorEnabled = true
                    tieEmailLayout.error = getString(R.string.empty_field)
                    tiePasswordLayout.isErrorEnabled = false
                }
            }
            if (viewModel.loginError.value == ValidationError.EMAIL_NOT_VALID) {
                binding.apply {
                    tieEmailLayout.isErrorEnabled = true
                    tieEmailLayout.error = getString(R.string.email_not_valid)
                    tiePasswordLayout.isErrorEnabled = false
                }
            }
            if (viewModel.loginError.value == ValidationError.PASSWORD_IS_EMPTY) {
                binding.apply {
                    tiePasswordLayout.isErrorEnabled = true
                    tiePasswordLayout.error = getString(R.string.empty_field)
                    tieEmailLayout.isErrorEnabled = false
                }
            }
            if (viewModel.loginError.value == ValidationError.PASSWORD_SHORT) {
                binding.apply {
                    tiePasswordLayout.isErrorEnabled = true
                    tiePasswordLayout.error = getString(R.string.password_short)
                    tieEmailLayout.isErrorEnabled = false
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSession { user ->
            if (user != null) {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }
    }

}