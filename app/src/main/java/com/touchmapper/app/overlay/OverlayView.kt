package com.touchmapper.app.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.touchmapper.app.R

@SuppressLint("InflateParams", "ClickableViewAccessibility")
class OverlayView(context: Context) {
    abstract inner class Overlay {
        abstract fun getView(): View
        abstract fun getLayoutParams(): WindowManager.LayoutParams

        fun toggleTouchable() {
            windowManager.updateViewLayout(
                getView(),
                getLayoutParams().apply {
                    flags =
                        if (flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE == WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) {
                            flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
                        } else {
                            flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        }
                }
            )
        }

        fun show() {
            val view = getView()
            val layoutParams = getLayoutParams()
            try {
                if (view.windowToken != null) return
                windowManager.addView(view, layoutParams)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun hide() {
            val view = getView()
            try {
                windowManager.removeView(view)
                view.invalidate()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val windowManager: WindowManager = context.getSystemService(WindowManager::class.java)

    private val buttonOverlay: Overlay
    private val settingOverlay: Overlay

    init {
        val layoutInflater = context.getSystemService(LayoutInflater::class.java)

        buttonOverlay = object : Overlay() {
            private val view = layoutInflater.inflate(R.layout.overlay_buttons, null)
            private val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.CENTER
            }

            override fun getView(): View = view

            override fun getLayoutParams(): WindowManager.LayoutParams = layoutParams
        }

        settingOverlay = @SuppressLint("RtlHardcoded")
        object : Overlay() {
            private val view = layoutInflater.inflate(R.layout.overlay_settings, null)
            private val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
            private var lastX = 0f
            private var lastY = 0f

            init {
                layoutParams.gravity = Gravity.TOP or Gravity.LEFT
                layoutParams.x = 100
                layoutParams.y = 100
                view.setOnTouchListener { _, event ->
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            lastX = event.rawX
                            lastY = event.rawY
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val dx = event.rawX - lastX
                            val dy = event.rawY - lastY
                            layoutParams.x += dx.toInt()
                            layoutParams.y += dy.toInt()
                            lastX = event.rawX
                            lastY = event.rawY
                            windowManager.updateViewLayout(getView(), getLayoutParams());
                        }
                    }
                    false
                }
            }

            override fun getView(): View = view

            override fun getLayoutParams(): WindowManager.LayoutParams = layoutParams
        }
    }

    fun show() {
        buttonOverlay.show()
        settingOverlay.show()
    }

    fun hide() {
        buttonOverlay.hide()
        settingOverlay.hide()
    }
}