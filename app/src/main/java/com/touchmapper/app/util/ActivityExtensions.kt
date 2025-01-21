package com.touchmapper.app.util

import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams

fun AppCompatActivity.edgeToEdge(view: View) {
    enableEdgeToEdge()
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = insets.left
            bottomMargin = insets.bottom
            rightMargin = insets.right
        }
        WindowInsetsCompat.CONSUMED
    }
}