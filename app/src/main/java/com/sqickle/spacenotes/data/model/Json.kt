package com.sqickle.spacenotes.data.model

import android.graphics.Color
import org.json.JSONObject

val Note.json: JSONObject
    get() = JSONObject().apply {
        put("uid", uid)
        put("title", title)
        put("content", content)

        if (color != Color.WHITE) {
            put("color", color)
        }

        if (importance != Importance.NORMAL) {
            put("importance", importance.name)
        }
    }