package com.emreyildirim.carmarketmobilee.utils

import android.content.Context
import android.util.Base64
import android.util.Log
import org.json.JSONObject

object JwtUtils {
    fun getClaimAsLong(context: Context, claim: String): Long? {
        return try {
            val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            val token = prefs.getString("jwt", null) ?: return null
            val parts = token.split(".")
            if (parts.size < 2) return null
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING))
            val json = JSONObject(payload)
            when {
                json.has(claim) -> json.optLong(claim)
                claim == "userId" && json.has("id") -> json.optLong("id")
                else -> null
            }
        } catch (e: Exception) {
            Log.e("JwtUtils", "Failed to parse JWT claim $claim", e)
            null
        }
    }
}














