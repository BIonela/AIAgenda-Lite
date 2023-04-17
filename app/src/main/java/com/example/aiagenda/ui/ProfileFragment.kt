package com.example.aiagenda.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.aiagenda.MainActivity
import android.Manifest
import android.widget.Toast
import com.example.aiagenda.R
import com.example.aiagenda.databinding.FragmentProfileBinding
import com.example.aiagenda.util.UiStatus
import com.example.aiagenda.viewmodel.AuthViewModel
import com.example.aiagenda.viewmodel.UiStateViewModel
import com.example.aiagenda.viewmodel.ViewModelFactory

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(requireActivity().application)
    }
    private val loadingViewModel: UiStateViewModel by activityViewModels()

    private val requestSinglePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                gallery.launch("image/*")
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.permission_deny),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private var gallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        authViewModel.getSession { user ->
            if (user != null) {
                if (uri != null) {
                    authViewModel.uploadPhoto(uri, user) { it, _ ->
                        if (it == UiStatus.SUCCESS) {
                            if (isAdded) {
                                binding.sivProfile.colorFilter = null
                                loadPhoto(uri.toString())
                                binding.sivProfile.visibility = View.VISIBLE
                                loadingViewModel.setSuccess()
                            }
                            binding.pbLoading.visibility = View.GONE
                        } else if (it == UiStatus.LOADING) {
                            binding.pbLoading.visibility = View.VISIBLE
                            binding.sivProfile.visibility = View.GONE
                            loadingViewModel.setLoading()
                        } else if (it == UiStatus.ERROR) {
                            binding.pbLoading.visibility = View.GONE
                            binding.sivProfile.visibility = View.GONE
                            loadingViewModel.setError()
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivLogout.setOnClickListener {
            authViewModel.logout {
                val i = Intent(activity, MainActivity::class.java)
                startActivity(i)
            }
        }

        loadUserData()

        binding.ivGallery.setOnClickListener {
            requestSinglePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun loadPhoto(uri: String) {
        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.progress_animation)
            .centerCrop()
            .into(binding.sivProfile)
    }

    private fun loadUserData() {
        authViewModel.getSession {
            authViewModel.user.observe(viewLifecycleOwner) { user ->
                binding.tvName.text = getString(
                    R.string.full_name,
                    user.first_name,
                    user.last_name
                )
                binding.tvLastName.text = getString(
                    R.string.last_name,
                    user.last_name
                )
                binding.tvFirstName.text = getString(
                    R.string.first_name,
                    user.first_name
                )
                binding.tvEmail.text = getString(
                    R.string.email,
                    user.email
                )
                binding.tvStudyYear.text = getString(
                    R.string.study_year,
                    user.study_year
                )
                if (user.photo_url != "") {
                    binding.sivProfile.colorFilter = null
                    loadPhoto(user.photo_url)
                } else {
                    binding.sivProfile.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.orange_medium
                        )
                    )
                    binding.sivProfile.setImageResource(R.drawable.ic_person)
                    binding.sivProfile.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                }
            }
        }
    }
}