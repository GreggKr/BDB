package me.greggkr.bdb.commands.admin

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandAttribute
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.util.Emoji
import me.greggkr.bdb.logging.LogType
import me.greggkr.bdb.util.toHex
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.entities.TextChannel
import org.jetbrains.kotlin.cli.common.toBooleanLenient
import java.awt.Color

@CommandDescription(name = "settings", triggers = [
    "settings", "s"
], attributes = [
    CommandAttribute(key = "adminOnly"),
    CommandAttribute(key = "hideFromHelp")
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
            "prefix" -> {
                if (a.size < 2) { // 0 -> prefix, 1 -> new prefix
                    channel.sendMessage("Current prefix: `${data.getPrefix(guild)}`.").queue()
                    return
                }

                val prefix = a[1]

                data.setPrefix(guild, prefix)
                channel.sendMessage("${Emoji.WHITE_CHECK_MARK} Set prefix to `$prefix`.").queue()
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

            "modlog" -> {
                if (a.size < 2) { // 0 -> modlog, 1 -> other
                    sendModLogHelp(guild, channel)
                    return
                }

                when (a[1].toLowerCase()) {
                    "channel" -> {
                        val channels = message.mentionedChannels

                        if (channels.isEmpty()) {
                            channel.sendMessage("${Emoji.X} Correct Usage: ${data.getPrefix(guild)}settings modlog channel <channel>").queue()
                            return
                        }

                        val ch = channels[0]
                        data.setModLogChannel(guild, ch)
                        channel.sendMessage("${Emoji.WHITE_CHECK_MARK} Set mod log channel to ${ch.asMention}.").queue()
                        return
                    }

                    "all" -> {
                        if (a.size < 4) {
                            channel.sendMessage("${Emoji.X} Correct Usage: ${data.getPrefix(guild)}settings modlog all <true/false>").queue()
                            return
                        }
                        val enabled = a[2].toBooleanLenient()
                        if (enabled == null) {
                            channel.sendMessage("${Emoji.X} Correct Usage: ${data.getPrefix(guild)}settings modlog all <true/false>").queue()
                            return
                        }

                        for (type in LogType.values()) {
                            data.setLogTypeEnabled(guild, type, enabled)
                        }
                    }

                    else -> {
                        if (a.size < 3) { // 0 -> modlog, 1 -> type, 2 -> enabled
                            channel.sendMessage("${Emoji.X} Correct Usage: ${data.getPrefix(guild)}settings modlog <type> <true/false>").queue()
                            return
                        }

                        try {
                            val type = LogType.valueOf(a[1].toUpperCase())
                            val enabled = a[2].toBooleanLenient()

                            if (enabled == null) {
                                channel.sendMessage("${Emoji.X} Correct Usage: ${data.getPrefix(guild)}settings modlog <type> <true/false>").queue()
                                return
                            }

                            data.setLogTypeEnabled(guild, type, enabled)

                            channel.sendMessage("${Emoji.WHITE_CHECK_MARK} Set `${type.name}` enabled to `$enabled`.").queue()
                        } catch (e: IllegalArgumentException) {
                            sendModLogHelp(guild, channel)
                        }

                        return
                    }
                }
            }

            "modrole" -> {
                val limitedArgs = args.split(Regex("\\s+"), 2)

                if (limitedArgs.size < 2) { // 0 -> modrole, 1 -> role
                    channel.sendMessage("Current Mod Role: `${data.getModRole(guild)?.name ?: "None"}`").queue()
                    return
                }

                val roleName = limitedArgs[1]
                val roles = guild.getRolesByName(roleName, true)
                if (roles.isEmpty()) {
                    channel.sendMessage("${Emoji.X} Correct Usage: ${data.getPrefix(guild)}settings modrole <role>").queue()
                    return
                }

                val role = roles[0]
                data.setModRole(guild, role)
                channel.sendMessage("${Emoji.WHITE_CHECK_MARK} Set mod role to `${role.name}`.").queue()
            }

            else -> {
                sendSettings(channel)
                return
            }
        }
    }

    private fun sendModLogHelp(guild: Guild, channel: MessageChannel) {
        val mlCh = data.getModLogChannel(guild)
        val embed = EmbedBuilder()
                .setColor(data.getColor(guild))
                .addField("Channel", if (mlCh == null) "Not set" else mlCh.asMention, true)

        val sb = StringBuilder()
        for (type in LogType.values()) {
            sb.append(type.name.toLowerCase())
                    .append(": ")
                    .append(data.getLogTypeEnabled(guild, type))
                    .append("\n")
        }

        embed.addField("Enabled", sb.toString().trim(), true)

        channel.sendMessage(embed.build()).queue()
    }

    private fun sendSettings(channel: MessageChannel) {
        val guild = (channel as TextChannel).guild
        val color = data.getColor(guild)
        val prefix = data.getPrefix(guild)

        channel.sendMessage(EmbedBuilder()
                .setColor(color)
                .addField("Prefix", prefix, true)
                .addField("Mod Role", data.getModRole(guild)?.asMention ?: "None", true)
                .addField("Mod Log", "${prefix}settings modlog", true)
                .build())
                .queue()
    }
}