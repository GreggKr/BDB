package me.greggkr.bdb.util.twitch.`object`

import com.google.gson.annotations.SerializedName

data class Game(
        val id: String,
        val name: String,
        @SerializedName("box_art_url") val icon: String
) {
    fun getIcon(width: Int = 256, height: Int = 256): String {
        return icon
                .replace("{width}", width.toString(), true)
                .replace("{height}", height.toString(), true)
    }
}