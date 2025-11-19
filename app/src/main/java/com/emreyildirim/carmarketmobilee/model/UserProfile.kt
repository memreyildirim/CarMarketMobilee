package com.emreyildirim.carmarketmobilee.model

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName(value = "id", alternate = ["userId", "userID"]) val id: Long,
    @SerializedName(value = "username", alternate = ["name"]) val username: String,
    @SerializedName("email") val email: String? = null,  // nullable çünkü her zaman gelmeyebilir
    @SerializedName(value = "role", alternate = ["roles", "authorities"]) val role: String
)