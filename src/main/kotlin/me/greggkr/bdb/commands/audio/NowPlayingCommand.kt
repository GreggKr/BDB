package me.greggkr.bdb.commands.audio

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.lavaLink
import me.greggkr.bdb.util.Emoji
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "nowplaying", triggers = [
    "nowplaying", "np"
], description = "Displays information about the currently playing song.")
class NowPlayingCommand : Command {
    override fun execute(message: Message, args: String) {
        val channel = message.channel

        val link = lavaLink.getExistingLink(message.guild)
        if (link == null) {
            channel.sendMessage("${Emoji.X} Not playing anything.").queue()
            return
        }

        val player = link.player
        if (player == null || player.playingTrack == null) {
            channel.sendMessage("${Emoji.X} Not playing anything.").queue()
            return
        }

        val playing = player.playingTrack
        val info = playing.info

        channel.sendMessage(EmbedBuilder()
                .setColor(data.getColor(message.guild))
                .setTitle(info.title, info.uri)
                .setDescription("Source: ${playing.sourceManager.sourceName}\n" +
                        "Author: ${info.author}\n" +
                        "Position: ${(player.trackPosition.toDouble() / info.length) * 100.0}%\n")
                .build())
                .queue()
    }
}