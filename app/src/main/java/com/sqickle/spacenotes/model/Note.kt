package com.sqickle.spacenotes.model

import android.graphics.Color
import org.json.JSONObject
import java.util.UUID

data class Note(
    val uid: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val color: Int = Color.WHITE,
    val importance: Importance = Importance.NORMAL
){
    companion object {
        fun parse(json: JSONObject): Note? {
            return try {
                Note(
                    uid = json.optString("uid", UUID.randomUUID().toString()),
                    title = json.getString("title"),
                    content = json.getString("content"),
                    color = json.optInt("color", Color.WHITE),
                    importance = when (json.optString("importance")) {
                        "HIGH" -> Importance.HIGH
                        "LOW" -> Importance.LOW
                        else -> Importance.NORMAL
                    }
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}