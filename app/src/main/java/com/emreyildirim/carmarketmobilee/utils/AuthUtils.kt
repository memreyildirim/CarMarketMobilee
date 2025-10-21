package com.emreyildirim.carmarketmobilee.utils

import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject

fun extractRolesFromJwt(token: String): List<String> {
    val parts = token.split(".")
    if (parts.size < 2) return emptyList()
    val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP))
    val payload = JSONObject(payloadJson)

    // Backend claim adlarını kendi backend’ine göre ayarla:
    // "role": "ADMIN" | "USER"
    // veya "roles": ["ADMIN","USER"]
    when {
        payload.has("roles") -> {
            val arr = payload.optJSONArray("roles") ?: JSONArray()
            return (0 until arr.length()).mapNotNull { arr.optString(it, null) }
        }
        payload.has("authorities") -> {
            val arr = payload.optJSONArray("authorities") ?: JSONArray()
            return (0 until arr.length()).mapNotNull { arr.optString(it, null) }
        }
        payload.has("role") -> {
            return listOfNotNull(payload.optString("role", null))
        }
        else -> return emptyList()
    }
}