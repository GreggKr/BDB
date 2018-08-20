package me.greggkr.bdb.commands.admin

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandAttribute
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.util.Emoji
import me.greggkr.bdb.util.toHex
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.entities.TextChannel
import java.awt.Color

@CommandDescription(name = "settings", triggers = [
    "settings", "s"
], attributes = [
    CommandAttribute(key = "adminOnly")
], description = "Allows you to change guild-specific bot settings,")
class SettingsCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        if (args.isEmpty()) {
            sendSettings(channel)
            return
        }

        val a = args.split(Regex("\\s+"))
        when (a[0].toLowerCase()) {
            "modlogchannel", "mlc" -> {
                if (a.size < 2) { // 0 -> mlc, 1 -> channel
                    channel.sendMessage("Current mod log channel: " + data.getModLogChannel(guild)?.asMention).queue()
                    return
                }

                val mentioned = message.mentionedChannels
                if (mentioned.isEmpty()) {
                    channel.sendMessage(" ${Emoji.X} Correct Usage: ${data.getPrefix(guild)}settings mlc <channel>.").queue()
                    return
                }

                val ch = mentioned[0]
                data.setModLogChannel(guild, ch)
                channel.sendMessage("${Emoji.WHITE_CHECK_MARK} Set mod log channel to ${ch.asMention}").queue()
                return
            }

            "color" -> {
                if (a.size < 2) { // 0 -> color, 1 -> color
                    channel.sendMessage("Current color: `${data.getColor(guild).toHex()}").queue()
                    return
                }

                try {
                    val color = Color.decode(a[1])

                    if (color == null) {
                        channel.sendMessage("${Emoji.X} Invalid color.").queue()
                        return
                    }

                    data.setColor(guild, color)
                    channel.sendMessage("${Emoji.WHITE_CHECK_MARK} Set color to `${color.toHex()}`.").queue()
                } catch (e: NumberFormatException) {
                    channel.sendMessage("${Emoji.X} Correct Usage: ${data.getPrefix(guild)}settings color <hex color>.").queue()
                }

                return
            }

            else -> {
                sendSettings(channel)
                return
            }
        }
    }

    private fun sendSettings(channel: MessageChannel) {
        val guild = (channel as TextChannel).guild
        val color = data.getColor(guild)

        channel.sendMessage(EmbedBuilder()
                .setColor(color)
                .addField("Prefix", data.getPrefix(guild), true)
                .addField("Mod Log Channel", data.getModLogChannel(guild)?.asMention ?: "None", true)
                .build())
                .queue()
    }
}