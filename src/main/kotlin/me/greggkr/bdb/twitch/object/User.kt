package me.greggkr.bdb.twitch.`object`

import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("id") val id: Int,
        @SerializedName("login") val login: String,
        @SerializedName("display_name") val displayName: String,
        @SerializedName("type") val type: String,
        @SerializedName("broadcaster_type") val broadcasterType: String,
        @SerializedName("description") val description: String,
        @SerializedName("profile_image_url") val profileImageUrl: String,
        @SerializedName("offline_image_url") val offlineImageUrl: String,
        @SerializedName("view_count") val viewCount: Int,
        @SerializedName("email") val email: String
)