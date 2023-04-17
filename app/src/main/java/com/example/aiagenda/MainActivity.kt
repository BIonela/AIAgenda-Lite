package com.example.aiagenda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.aiagenda.databinding.ActivityMainBinding
import com.example.aiagenda.internet.LiveDataInternetConnections
import com.example.aiagenda.util.UiStatus
import com.example.aiagenda.viewmodel.UiStateViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var connection: LiveDataInternetConnections

    private val uiStateViewModel: UiStateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        observeInternet()
        showBottomNavigationMenu()
    }

    private fun observeInternet() {
        connection = LiveDataInternetConnections(application)

        if (connection.value == null) {
            binding.tvNoInternet.visibility = View.VISIBLE
            binding.ivNoInternet.visibility = View.VISIBLE
        }

        connection.observe(this) { isConnected ->
            if (!isConnected || isConnected == null) {
                binding.tvNoInternet.visibility = View.VISIBLE
                binding.ivNoInternet.visibility = View.VISIBLE
            } else {
                binding.tvNoInternet.visibility = View.GONE
                binding.ivNoInternet.visibility = View.GONE
            }
        }

    }

    private fun showBottomNavigationMenu() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.idNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomMenu = binding.bnvMenu
        bottomMenu.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            if (nd.id == R.id.loginFragment
                || nd.id == R.id.signUpFragment
                || nd.id == R.id.forgotPasswordFragment
                || nd.id == R.id.dialogFragment
                || nd.id == R.id.taskDetailsFragment
                || nd.id == R.id.createTaskFragment
            ) {
                binding.bnvMenu.visibility = View.GONE
            } else {
                binding.bnvMenu.visibility = View.VISIBLE
            }
        }

        uiStateViewModel.uiState.observe(this) { state ->
            if (state == UiStatus.LOADING) {
                binding.bnvMenu.visibility = View.GONE
            } else if (state == UiStatus.SUCCESS) {
                binding.bnvMenu.visibility = View.VISIBLE
            } else {
                binding.bnvMenu.visibility = View.GONE
                Toast.makeText(
                    this,
                    resources.getString(
                        R.string.reconnect_error
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}