package com.rus_euphoria.notes.model

import android.graphics.Color
import org.json.JSONObject
import java.util.Date

val Note.json: JSONObject
    get() {
        val result = JSONObject()
        result.put("uid", uid)
        result.put("title", title)
        result.put("content", content)
        if (color != Color.WHITE) {
            result.put("color", color)
        }
        if (importance != Importance.NORMAL) {
            result.put("importance", importance.name)
        }
        if (selfDestructDate != null) {
            result.put("selfDestructDate", selfDestructDate.time)
        }
        return result
    }

fun Note.Companion.parse(json: JSONObject): Note? {
    return try {
        val selfDestructDate = if (json.has("selfDestructDate")) {
            Date(json.getLong("selfDestructDate"))
        } else {
            null
        }

        Note(
            uid = json.getString("uid"),
            title = json.getString("title"),
            content = json.getString("content"),
            color = if (json.has("color")) json.getInt("color") else Color.WHITE,
            importance = if (json.has("importance")) {
                Importance.valueOf(json.getString("importance"))
            } else {
                Importance.NORMAL
            },
            selfDestructDate = selfDestructDate
        )
    } catch (e: Exception) {
        null
    }
}
