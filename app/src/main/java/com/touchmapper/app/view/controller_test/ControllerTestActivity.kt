package com.touchmapper.app.view.controller_test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.touchmapper.app.R
import com.touchmapper.app.databinding.ActivityControllerTestBinding
import kotlin.math.abs

class ControllerTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityControllerTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControllerTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val mInputDevice = event.device

        binding.deviceNameTextView.text = mInputDevice.name
        binding.deviceIdTextView.text = mInputDevice.descriptor

        val handled = false
        if ((event.source and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
            if (event.repeatCount == 0 && event.keyCode != KeyEvent.KEYCODE_DPAD_CENTER && event.keyCode != KeyEvent.KEYCODE_DEL && event.keyCode != KeyEvent.KEYCODE_SPACE && event.keyCode != KeyEvent.KEYCODE_SPACE) {
                binding.pressedKeyTextView.text = getString(R.string.pressed_key, event.keyCode)
            }
            if (handled) {
                return true
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE) {
            val historySize = event.historySize
            for (i in 0 until historySize) {
                // Process the event at historical position i
                processJoystickInput(event, i)
            }

            processJoystickInput(event, -1)
        }
        return super.onGenericMotionEvent(event)
    }

    private fun processJoystickInput(event: MotionEvent, historyPos: Int) {
        val mInputDevice = event.device
        binding.deviceIdTextView.text = mInputDevice.descriptor
        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.
        val axis_x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_X, historyPos)
        binding.axisXTextView.text = getString(R.string.axis_x, axis_x)
        val axis_hat_x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_HAT_X, historyPos)
        binding.axisXHatTextView.text = getString(R.string.axis_hat_x, axis_hat_x)
        val axis_z = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_Z, historyPos)
        binding.axisZTextView.text = getString(R.string.axis_z, axis_z)

        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch, or the right control stick.
        val axis_y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_Y, historyPos)
        binding.axisYTextView.text = getString(R.string.axis_y, axis_y)
        val axis_hat_y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_HAT_Y, historyPos)
        binding.axisYHatTextView.text = getString(R.string.axis_hat_y, axis_hat_y)
        val axis_rz = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_RZ, historyPos)
        binding.axisRZTextView.text = getString(R.string.axis_rz, axis_rz)

        val axis_rtrigger =
            getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_RTRIGGER, historyPos)
        binding.axisRTriggerTextView.text = getString(R.string.axis_rtrigger, axis_rtrigger)
        val axis_ltrigger =
            getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_LTRIGGER, historyPos)
        binding.axisLTriggerTextView.text = getString(R.string.axis_ltrigger, axis_ltrigger)
        val axis_throttle =
            getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_THROTTLE, historyPos)
        binding.axisThrottleTextView.text = getString(R.string.axis_throttle, axis_throttle)
        val axis_brake = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_BRAKE, historyPos)
        binding.axisBreakTextView.text = getString(R.string.axis_brake, axis_brake)
    }

    companion object {
        fun openActivity(context: Context) {
            val i = Intent(context, ControllerTestActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(i)
        }

        private fun getCenteredAxis(
            event: MotionEvent,
            device: InputDevice,
            axis: Int,
            historyPos: Int
        ): Float {
            val range = device.getMotionRange(axis, event.source)

            // A joystick at rest does not always report an absolute position of
            // (0,0). Use the getFlat() method to determine the range of values
            // bounding the joystick axis center.
            if (range != null) {
                val flat = range.flat
                val value =
                    if (historyPos < 0) event.getAxisValue(axis) else event.getHistoricalAxisValue(
                        axis,
                        historyPos
                    )

                // Ignore axis values that are within the 'flat' region of the
                // joystick axis center.
                if (abs(value.toDouble()) > flat) {
                    return value
                }
            }
            return 0F
        }
    }
}
