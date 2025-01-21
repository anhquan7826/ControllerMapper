package com.touchmapper.app.view.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.touchmapper.app.databinding.ActivityMainBinding
import com.touchmapper.app.overlay.OverlayService
import com.touchmapper.app.util.edgeToEdge
import com.touchmapper.app.view.controller_test.ControllerTestActivity
import com.touchmapper.app.view.input_test.InputListenerActivity

class HomeActivity : AppCompatActivity() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val permissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            viewModel.setNotificationPermissionStatus(isGranted)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        edgeToEdge(binding.root)
        viewModel.checkShizukuPermission()

        buildObserver()
        buildEventListener()
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                viewModel.setNotificationPermissionStatus(true)
            } else {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        viewModel.setOverlayPermissionStatus(Settings.canDrawOverlays(this))
    }

    private fun buildObserver() {
        viewModel.state.observe(this) {
            binding.textShizukuStatus.text =
                if (it.shizukuPermissionStatus == null)
                    "UNAVAILABLE"
                else if (it.shizukuPermissionStatus)
                    "GRANTED"
                else
                    "DENIED"

            binding.textOverlayStatus.text =
                if (it.overlayPermissionStatus)
                    "GRANTED"
                else
                    "DENIED"
        }
    }

    private fun buildEventListener() {
        binding.buttonOpenTest.setOnClickListener {
            ControllerTestActivity.openActivity(this@HomeActivity)
        }
        binding.buttonRequestShizuku.setOnClickListener {
            viewModel.checkShizukuPermission()
        }
        binding.buttonRequestOverlay.setOnClickListener {
            viewModel.checkOverlayPermission(this@HomeActivity)
        }
        binding.buttonLaunchOverlay.setOnClickListener {
            OverlayService.start(this@HomeActivity)
        }
        binding.buttonInputListener.setOnClickListener {
            startActivity(Intent(
                this@HomeActivity,
                InputListenerActivity::class.java
            ))
        }
    }
}
