package me.greggkr.bdb.commands.audio

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.lavaLink
import me.greggkr.bdb.util.Emoji
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "pause", triggers = [
    "pause", "resume"
], description = "Plays/Resumes playing track")
class PauseCommand : Command {
    override fun execute(message: Message, args: String) {
        val channel = message.channel

        val link = lavaLink.getExistingLink(message.guild)
        if (link == null) {
            channel.sendMessage("${Emoji.X} No music.").queue()
            return
        }

        val player = link.player
        if (player.playingTrack == null) {
            channel.sendMessage("${Emoji.X} No music.").queue()
            return
        }

        if (player.isPaused) {
            player.isPaused = false
            channel.sendMessage("${Emoji.WHITE_CHECK_MARK} Resumed.").queue()
        } else {
            player.isPaused = true
            channel.sendMessage("${Emoji.WHITE_CHECK_MARK} Paused.").queue()
        }
    }
}