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
