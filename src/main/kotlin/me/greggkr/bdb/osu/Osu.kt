package me.greggkr.bdb.osu

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.oopsjpeg.osu4j.GameMod
import me.greggkr.bdb.config
import me.greggkr.bdb.data
import me.greggkr.bdb.util.Config
import me.greggkr.bdb.util.Emoji
import net.dv8tion.jda.core.entities.Message
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

private val API_KEY = config[Config.Osu.apiKey]

private val gson = GsonBuilder().create()
private val client = OkHttpClient.Builder()
        .authenticator { _, response ->
            response
                    .request()
                    .newBuilder()
                    .url(response
                            .request()
                            .url()
                            .newBuilder()
                            .addQueryParameter("k", API_KEY)
                            .build()
                    ).build()
        }
        .build()

data class RecentScore(
        @SerializedName("beatmap_id")
        val beatmapId: String,
        @SerializedName("count100")
        val count100: String,
        @SerializedName("count300")
        val count300: String,
        @SerializedName("count50")
        val count50: String,
        @SerializedName("countgeki")
        val countgeki: String,
        @SerializedName("countkatu")
        val countkatu: String,
        @SerializedName("countmiss")
        val countmiss: String,
        @SerializedName("date")
        val date: String,
        @SerializedName("enabled_mods")
        val enabledMods: String,
        @SerializedName("maxcombo")
        val maxcombo: String,
        @SerializedName("perfect")
        val perfect: String,
        @SerializedName("rank")
        val rank: String,
        @SerializedName("score")
        val score: String,
        @SerializedName("user_id")
        val userId: String
)

enum class UserType(val apiName: String) {
    ID("id"), USERNAME("string")
}

class Osu {
    companion object {

        fun prettyMods(mods: Array<GameMod>): String {
            return if (mods.isEmpty()) {
                ""
            } else {
                "+" + mods.joinToString(separator = "") { it.shortName }
            }
        }

        fun prettyRank(rank: String): String {
            return rank.replace("X", "SS")
        }

        fun getOsuUser(message: Message, a: List<String>): String? {
            val guild = message.guild
            val channel = message.channel

            var inputUser = if (!message.mentionedUsers.isEmpty()) {
                data.getOsuUser(guild, message.mentionedUsers[0])
            } else {
                if (a.isNullOrEmpty() || a[0].isEmpty()) {
                    data.getOsuUser(guild, message.author)
                } else {
                    a[0]
                }
            }

            if (inputUser == null) {
                inputUser = data.getOsuUser(guild, message.author)
                if (inputUser == null) {
                    channel.sendMessage("${Emoji.X} You must supply a valid user. Either the person you mentioned or you do not have a linked user. Use ${data.getPrefix(guild)}user <username>.").queue()
                }
            }

            return inputUser
        }

        private fun makeRequest(req: Request): JsonElement? {
            val res = client.newCall(req).execute()
            val body = res.body() ?: return null

            return gson.fromJson(body.string(), JsonElement::class.java)
        }
    }
}
