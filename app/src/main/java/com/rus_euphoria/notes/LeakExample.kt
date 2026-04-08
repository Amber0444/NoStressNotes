package com.rus_euphoria.notes

import android.app.Activity
import android.os.Handler
import android.os.Looper
import org.slf4j.LoggerFactory

class LeakyHandler(private val activity: Activity) {
    private val log = LoggerFactory.getLogger(LeakyHandler::class.java)
    private val handler = Handler(Looper.getMainLooper())

    fun startLeaking() {
        handler.postDelayed({
            log.info("Handler fired for activity: ${activity.localClassName}")
        }, 5 * 60 * 1000L)
        log.warn("LEAK: posted delayed Runnable holding Activity reference for 5 minutes")
    }
}
