package com.touchmapper.app.view.input_test

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.touchmapper.app.R
import com.touchmapper.app.databinding.ActivityInputListenerBinding
import com.touchmapper.app.util.edgeToEdge
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class InputListenerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputListenerBinding
    private lateinit var thread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputListenerBinding.inflate(layoutInflater)
        edgeToEdge(binding.root)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        listenInput()
    }

    override fun onDestroy() {
        if (this::thread.isInitialized) {
            thread.interrupt()
        }
        super.onDestroy()
    }

    private fun listenInput() {
        thread = thread {
            try {
                val sh = Runtime.getRuntime().exec("su")
                val buffer = BufferedReader(InputStreamReader(sh.inputStream))
                val err = BufferedReader(InputStreamReader(sh.errorStream))
                thread {
                    try {
                        Log.d("InputListener", "Listening for sh")
                        while (true) {
                            val line = buffer.readLine()
                            if (line != null && !line.contains("SYN_REPORT")) {
                                Log.d("InputListener", line)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                thread {
                    try {
                        Log.d("InputListener", "Listening for sh err stream")
                        while (true) {
                            val line = err.readLine()
                            if (line != null) {
                                Log.e("InputListener", line)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                val outStream = DataOutputStream(sh.outputStream)
                outStream.writeBytes("su -c getevent -ql\n")
                outStream.flush()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}