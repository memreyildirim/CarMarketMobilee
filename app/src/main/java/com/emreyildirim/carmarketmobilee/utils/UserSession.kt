package com.emreyildirim.carmarketmobilee.utils

import android.content.Context
import android.util.Log
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance

object UserSession {
    private const val PREFS = "auth"
    private const val KEY_USER_ID = "userId"

    fun getCachedUserId(context: Context): Long? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val id = prefs.getLong(KEY_USER_ID, -1L)
        return if (id > 0) id else null
    }

    fun cacheUserId(context: Context, userId: Long) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putLong(KEY_USER_ID, userId).apply()
    }

    suspend fun getOrFetchUserId(context: Context): Long {
        val cached = getCachedUserId(context)
        if (cached != null) return cached
        val profile = RetrofitInstance.getUserService(context).getProfile()
        Log.d("UserSession", "Fetched profile id=${profile.id} username=${profile.username} role=${profile.role}")
        val resolvedId = if (profile.id > 0) {
            profile.id
        } else {
            // Fallback: try to read userId from JWT claims
            JwtUtils.getClaimAsLong(context, "userId") ?: 0L
        }
        if (resolvedId > 0) {
            cacheUserId(context, resolvedId)
            return resolvedId
        }
        // Final fallback: log and return 0 (caller should handle as error)
        Log.e("UserSession", "Could not resolve userId from profile or JWT; defaulting to 0")
        return 0L
    }
}


