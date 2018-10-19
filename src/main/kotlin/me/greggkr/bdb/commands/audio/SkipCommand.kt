package me.greggkr.bdb.commands.audio

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.audio.queues
import me.greggkr.bdb.lavaLink
import me.greggkr.bdb.util.Emoji
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "skip", triggers = [
    "skip", "s"
], description = "Allows you to skip a song.")
class SkipCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        val link = lavaLink.getExistingLink(guild)
        if (link == null) {
            channel.sendMessage("${Emoji.X} Not playing anything.").queue()
            return
        }

        val player = link.player
        if (player == null) {
            channel.sendMessage("${Emoji.X} Not playing anything.").queue()
            return
        }

        val next = queues[guild]?.singleOrNull()
        if (next == null) {
            player.stopTrack()
            channel.sendMessage("${Emoji.WHITE_CHECK_MARK} Stopped playing current track, nothing next.").queue()
        } else {
            player.playTrack(next)
            channel.sendMessage("${Emoji.WHITE_CHECK_MARK} Playing ${next.info.title}").queue()
        }
    }
}