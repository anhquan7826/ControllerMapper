package com.touchmapper.app

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.view.MotionEvent
import com.touchmapper.app.output.Server
import com.touchmapper.app.output.TouchMapper
import com.touchmapper.app.output.config.ConfigParser
import com.touchmapper.app.output.config.TouchConfig
import java.io.File
import java.io.FileNotFoundException

/**
 * Created by shyri on 06/09/17.
 */
class Main {
    private var touchMapper: TouchMapper? = null

    var messageHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                InputEventInjector.SOURCE_MOVEMENT -> touchMapper!!.processEvent(msg.obj as MotionEvent)
                InputEventInjector.SOURCE_KEY -> touchMapper!!.processEvent(msg.obj as KeyEvent)
            }
        }
    }

    private val server: Server

    init {
        var touchConfig: TouchConfig? = null
        try {
            touchConfig =
                readFile("/storage/self/primary/Android/data/es.shyri.touchmapper/files/mapping.json")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        try {
            touchMapper = TouchMapper(touchConfig)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        server = Server(messageHandler)
        server.start()
    }

    @Throws(FileNotFoundException::class)
    private fun readFile(fileName: String): TouchConfig {
        val configParser = ConfigParser()
        return configParser.parseConfig(File(fileName))
    }

    companion object {
        var looper: Looper? = null
        const val DEFAULT_PORT: Int = 6543

        @JvmStatic
        fun main(args: Array<String>) {
            Looper.prepare()
            looper = Looper.myLooper()

            val main = Main()

            Looper.loop()
        }
    }
}
