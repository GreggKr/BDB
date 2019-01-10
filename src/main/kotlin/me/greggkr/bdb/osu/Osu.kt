package me.greggkr.bdb.osu

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.oopsjpeg.osu4j.GameMod
import com.oopsjpeg.osu4j.OsuScore
import me.greggkr.bdb.config
import me.greggkr.bdb.data
import me.greggkr.bdb.starFormat
import me.greggkr.bdb.util.Config
import me.greggkr.bdb.util.Emoji
import net.dv8tion.jda.core.entities.Message
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Duration
import java.time.LocalDateTime

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

data class OsuUserArguments(val user: String?,
                            val params: List<String>
)

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

        fun prettyTime(score: OsuScore): String {
            val duration = Duration.between(score.date.toOffsetDateTime().toLocalDateTime(), LocalDateTime.now())

            val days = duration.toDays()
            val hours = duration.minusDays(days).toHours()
            val minutes = duration.minusHours(hours).toMinutes()
            val seconds = duration.minusMinutes(minutes).toMillis() * 1000 // very annoying

            var timeInfo = ""

            if (days > 0) {
                timeInfo += if (days > 1) "$days days "
                else "one day "
            }

            timeInfo += if (hours > 0) {
                if (hours > 1) "$hours hours "
                else "one hour "
            } else if (minutes > 0 ) {
                if (minutes > 1) "$minutes minutes "
                else "one minute "
            } else {
                if (seconds > 1) "$seconds seconds "
                else "one second "
            }

            return timeInfo + "ago"
        }

        fun playTitle(score: OsuScore): String {
            val map = score.beatmap.get()
            val mods = Osu.prettyMods(score.enabledMods)
            val timeInfo = Osu.prettyTime(score)

            return "${map.title} [${map.version}] $mods - ${starFormat.format(map.difficulty)}* ($timeInfo)"
        }

        fun getUserArguments(message: Message, args: String): OsuUserArguments {
            val a = args.split(Regex("\\s+\\|\\s+"))
            val guild = message.guild
            val channel = message.channel

            var specifiedUser = false
            var user = ""

            if (!a.isNullOrEmpty()) {
                if (!a[0].isEmpty()) {
                    if (a[0].contains("@") && !message.mentionedUsers.isEmpty()) {
                        val mentionedUser = data.getOsuUser(guild, message.mentionedUsers[0])
                        if (!mentionedUser.isNullOrEmpty()) {
                            user = mentionedUser
                            specifiedUser = true
                        }
                    }
                    // If it's a number assume it's a parameter not a username
                    if (!specifiedUser && !a[0].matches(Regex("\\d+"))) {
                        user = a[0]
                        specifiedUser = true
                    }
                }

                if (user.isEmpty()) {
                    val authorUser = data.getOsuUser(guild, message.author)
                    if (!authorUser.isNullOrEmpty()) {
                        user = authorUser
                    }
                }
            }

            if (user.isEmpty()) {
                channel.sendMessage("${Emoji.X} You must supply a valid user. Either the person you mentioned or you do not have a linked user. Use ${data.getPrefix(guild)}user <username>.").queue()
            }

            val p = if (specifiedUser) {
                if (a.size < 2) listOf()
                else a.subList(1, a.size)
            } else {
                a
            }

            return OsuUserArguments(user, p)
        }

        fun getNumberArgument(params: List<String>, default: Int, min: Int, max: Int, index: Int = 0): Int {
            if (params.size <= index) return default
            var tmp = params[index].toIntOrNull() ?: default

            if (tmp < min) {
                tmp = min
            }

            if (tmp > max) {
                tmp = max
            }

            return tmp
        }

        private fun makeRequest(req: Request): JsonElement? {
            val res = client.newCall(req).execute()
            val body = res.body() ?: return null

            return gson.fromJson(body.string(), JsonElement::class.java)
        }
    }
}
