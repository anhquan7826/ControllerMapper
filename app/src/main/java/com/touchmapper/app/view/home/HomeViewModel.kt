package com.touchmapper.app.view.home

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import rikka.shizuku.Shizuku

class HomeViewModel : ViewModel() {
    data class HomeViewState(
        val shizukuPermissionStatus: Boolean? = false,
        val overlayPermissionStatus: Boolean = false,
        val notificationPermissionStatus: Boolean = false
    )

    companion object {
        const val SHIZUKU_REQUEST_CODE = 1
    }

    private val _state = MutableLiveData(HomeViewState())
    val state: LiveData<HomeViewState>
        get() = _state

    init {
        Shizuku.addRequestPermissionResultListener { requestCode, result ->
            if (requestCode == SHIZUKU_REQUEST_CODE) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    _state.value = _state.value!!.copy(
                        shizukuPermissionStatus = true
                    )
                }
            }
        }
    }

    fun checkShizukuPermission() {
        if (!Shizuku.pingBinder()) {
            _state.value = _state.value!!.copy(
                shizukuPermissionStatus = null
            )
            return
        }
        if (Shizuku.isPreV11()) {
            return
        }
        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            _state.value = _state.value!!.copy(
                shizukuPermissionStatus = true
            )
        } else if (Shizuku.shouldShowRequestPermissionRationale()) {
            return
        } else {
            Shizuku.requestPermission(SHIZUKU_REQUEST_CODE)
        }
    }

    fun checkOverlayPermission(context: Context) {
        if (!Settings.canDrawOverlays(context)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            context.startActivity(intent)
        }
    }

    fun setOverlayPermissionStatus(result: Boolean) {
        _state.value = _state.value!!.copy(
            overlayPermissionStatus = result
        )
    }

    fun setNotificationPermissionStatus(result: Boolean) {
        _state.value = _state.value!!.copy(
            notificationPermissionStatus = result
        )
    }
}