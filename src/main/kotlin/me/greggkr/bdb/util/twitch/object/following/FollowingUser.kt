package me.greggkr.bdb.util.twitch.`object`.following

import com.google.gson.annotations.SerializedName
import java.util.*

data class FollowingUser(
        @SerializedName("from_id") val follower: String,
        @SerializedName("to_id") val streamer: String,
        @SerializedName("followed_at") val followedAt: Date
)