package me.greggkr.bdb.commands.audio

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.lavaLink
import me.greggkr.bdb.util.Emoji
import me.greggkr.bdb.util.toPercent
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "audiostats", triggers = [
    "audiostats"
], description = "Gets audio statistics")
class AudioStatsCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        val node = lavaLink.getLink(message.guild).getNode(true)
        if (node == null) {
            channel.sendMessage("${Emoji.X} Failed to get Lavalink node.").queue()
            return
        }

        val stats = node.stats
        if (stats == null) {
            channel.sendMessage("${Emoji.X} Failed to get Lavalink remote stats.").queue()
            return
        }
        
        channel.sendMessage(EmbedBuilder()
                .setColor(data.getColor(guild))
                .setTitle("Audio Statistics for ${guild.name}")
                .addField("Load", "Lavalink: ${stats.lavalinkLoad.toPercent()}\n" +
                        "System: ${stats.systemLoad.toPercent()}", true)
                .addField("Players", "Total: ${stats.players}\n" +
                        "Playing: ${stats.playingPlayers}", true)
                .addField("Frames/Min", "Sent: ${stats.avgFramesSentPerMinute}\n" +
                        "Nulled: ${stats.avgFramesNulledPerMinute}\n" +
                        "Deficit: ${stats.avgFramesDeficitPerMinute}", true)
                .build())
                .queue()
    }
}