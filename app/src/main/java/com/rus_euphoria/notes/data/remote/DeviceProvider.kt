package com.rus_euphoria.notes.data.remote

import android.content.Context
import java.util.UUID

class DeviceProvider(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val deviceId: String by lazy {
        prefs.getString(KEY_DEVICE_ID, null) ?: run {
            val id = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_DEVICE_ID, id).apply()
            id
        }
    }

    private companion object {
        const val PREFS_NAME = "notes_device_prefs"
        const val KEY_DEVICE_ID = "device_id"
    }
}
