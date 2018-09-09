package me.greggkr.bdb.commands.info

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.util.Emoji
import me.greggkr.bdb.util.twitch.Twitch
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel
import java.time.OffsetDateTime

@CommandDescription(name = "twitch", triggers = [
    "twitch"
], description = "Basic Twitch features")
class TwitchCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        if (args.isEmpty()) {
            sendHelp(channel)
            return
        }

        val a = args.split(Regex("\\s+"))
        if (a.isEmpty()) {
            sendHelp(channel)
            return
        }

        when (a[0].toLowerCase()) {
            "streaming", "isstreaming", "s" -> {
                if (a.size < 2) { // 0 -> streaming, 1 -> user
                    channel.sendMessage("${Emoji.X} Correct Usage: ${data.getPrefix(guild)}twitch streaming <user>").queue()
                    return
                }

                val streaming = Twitch.isStreaming(a[1])
                val user = Twitch.getUser(a[1])

                if (streaming == null || user == null) {
                    channel.sendMessage("${Emoji.X} Failed to get user `${a[1]}`.").queue()
                    return
                }

                channel.sendMessage(EmbedBuilder()
                        .setColor(data.getColor(guild))
                        .setThumbnail(user.profileImageUrl)
                        .setDescription("${if (streaming) Emoji.WHITE_CHECK_MARK else Emoji.X} `${user.displayName}` is ${if (streaming) "" else "not "}currently streaming.")
                        .build())
                        .queue()

                return
            }

            "user", "u" -> {
                if (a.size < 2) { // 0 -> streaming, 1 -> user
                    channel.sendMessage("${Emoji.X} Correct Usage: ${data.getPrefix(guild)}twitch streaming <user>").queue()
                    return
                }

                val streaming = Twitch.isStreaming(a[1])
                val user = Twitch.getUser(a[1])

                if (streaming == null || user == null) {
                    channel.sendMessage("${Emoji.X} Failed to get user `${a[1]}`.").queue()
                    return
                }

                channel.sendMessage(EmbedBuilder()
                        .setColor(data.getColor(guild))
                        .setThumbnail(user.profileImageUrl)
                        .addField("Username", user.displayName, true)
                        .addField("Streaming?", streaming.toString(), true)
                        .addField("View Count", user.viewCount.toString(), true)
                        .addField("Followers", Twitch.getFollowers(a[1])?.first?.toString() ?: "Failed to fetch", true)
                        .build())
                        .queue()
            }

            "game", "g" -> {
                if (a.size < 2) { // 0 -> game, 1 -> name
                    channel.sendMessage("${Emoji.X} Correct Usage: ${data.getPrefix(guild)}twitch game <game>").queue()
                    return
                }

                val name = a[1]
                val game = Twitch.getGame(name)

                if (game == null) {
                    channel.sendMessage("${Emoji.X} Failed to get game `$name`.")
                    return
                }

                channel.sendMessage(EmbedBuilder()
                        .setColor(data.getColor(guild))
                        .setThumbnail(game.getIcon())
                        .addField("Name", game.name, true)
                        .build())
                        .queue()

                return
            }

            else -> sendHelp(channel)
        }
    }

    private fun sendHelp(channel: MessageChannel) {
        channel.sendMessage("${Emoji.X} currently unsupported and im too lazy to write the help for this rn").queue()
    }
}