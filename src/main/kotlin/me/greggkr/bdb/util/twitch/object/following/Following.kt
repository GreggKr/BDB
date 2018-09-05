package me.greggkr.bdb.util.twitch.`object`.following

import com.google.gson.annotations.SerializedName
import java.util.*

data class Following(
        val total: Int,
        @SerializedName("data") val followers: Array<FollowingUser>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Following

        if (total != other.total) return false
        if (!Arrays.equals(followers, other.followers)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = total
        result = 31 * result + Arrays.hashCode(followers)
        return result
    }
}