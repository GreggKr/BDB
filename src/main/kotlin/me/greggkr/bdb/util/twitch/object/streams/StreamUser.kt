package me.greggkr.bdb.util.twitch.`object`.streams

import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime

data class StreamUser(
        @SerializedName("id") val id: String,
        @SerializedName("user_id") val userId: String,
        @SerializedName("game_id") val gameId: String,
        @SerializedName("community_ids") val communityIds: List<String>,
        @SerializedName("type") val type: String,
        @SerializedName("title") val title: String,
        @SerializedName("viewer_count") val viewerCount: Int,
        @SerializedName("started_at") val startedAt: String,
        @SerializedName("language") val language: String,
        @SerializedName("thumbnail_url") val thumbnailUrl: String
)