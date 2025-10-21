package com.emreyildirim.carmarketmobilee.utils

import org.json.JSONObject
import java.util.concurrent.TimeUnit
import android.util.Base64

fun isJwtExpired(token: String): Boolean {
    return try {
        val parts = token.split(".")
        if (parts.size < 2) return true
        val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP))
        val expSeconds = JSONObject(payloadJson).optLong("exp", 0L)
        if (expSeconds <= 0L) false // exp yoksa “geçerli say” veya true yapmayı seçebilirsin
        else {
            val nowSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
            nowSeconds >= expSeconds
        }
    } catch (_: Exception) {
        true
    }
}