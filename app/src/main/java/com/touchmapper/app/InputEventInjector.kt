package com.touchmapper.app

import android.annotation.SuppressLint
import android.hardware.input.InputManager
import android.os.Build
import android.os.SystemClock
import android.view.InputEvent
import android.view.MotionEvent
import android.view.MotionEvent.PointerCoords
import android.view.MotionEvent.PointerProperties
import androidx.core.view.InputDeviceCompat
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

@SuppressLint("DiscouragedPrivateApi")
class InputEventInjector {
    object InputManagerMethods {
        const val GET_INSTANCE = "getInstance"
        const val INJECT_INPUT_EVENT = "injectInputEvent"
    }

    companion object {
        const val SOURCE_KEY: Int = 2
        const val SOURCE_MOVEMENT: Int = 1
    }

    private var inputManagerClass: Class<*>
    private var inputManagerInstance: InputManager
    private var injectInputEventMethod: Method

    init {
        //Get the instance of InputManager class using reflection
        inputManagerClass = getInputManagerClass()
        inputManagerInstance = getInputManagerInstance(inputManagerClass)
        injectInputEventMethod = getInjectInputEventMethod(inputManagerClass)

        //Make MotionEvent.obtain() method accessible
        val methodName = "obtain"
        MotionEvent::class.java.getDeclaredMethod(methodName, *arrayOfNulls(0)).isAccessible =
            true
    }

    @SuppressLint("PrivateApi")
    private fun getInputManagerClass(): Class<*> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            Class.forName("android.hardware.input.InputManagerGlobal")
        } else {
            InputManager::class.java
        }
    }

    @SuppressLint("PrivateApi")
    private fun getInputManagerInstance(inputManagerClass: Class<*>): InputManager {
        return inputManagerClass.getDeclaredMethod(InputManagerMethods.GET_INSTANCE)
            .invoke(null, *arrayOfNulls(0)) as InputManager
    }

    private fun getInjectInputEventMethod(inputManagerClass: Class<*>): Method {
        return inputManagerClass.getMethod(
            InputManagerMethods.INJECT_INPUT_EVENT,
            *arrayOf(
                InputEvent::class.java,
                Integer.TYPE
            )
        )
    }

    @Throws(InvocationTargetException::class, IllegalAccessException::class)
    fun injectTouch(
        action: Int,
        pointerProperties: Array<PointerProperties?>?,
        pointerCoords: Array<PointerCoords?>?
    ) {
        val timestamp = SystemClock.uptimeMillis()
        val event =
            MotionEvent.obtain(
                timestamp,
                timestamp,
                action,
                pointerProperties?.size ?: 0,
                pointerProperties,
                pointerCoords,
                0,
                0,
                1.0f,
                1.0f,
                0,
                0,
                InputDeviceCompat.SOURCE_TOUCHSCREEN,
                0
            )
        event.source = InputDeviceCompat.SOURCE_TOUCHSCREEN;
        injectInputEventMethod.invoke(inputManagerInstance, listOf(event, Integer.valueOf(2)));
    }

    val inputDevices: IntArray
        get() = inputManagerInstance.inputDeviceIds
}
