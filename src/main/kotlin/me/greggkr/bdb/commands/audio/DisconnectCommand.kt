package me.greggkr.bdb.commands.audio

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.lavaLink
import me.greggkr.bdb.util.Emoji
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "disconnect", triggers = [
    "disconnect"
], description = "Disconnects the bot from your voice channel.")
class DisconnectCommand : Command {
    override fun execute(message: Message, args: String) {
        val channel = message.channel
        val voice = message.member.voiceState

        if (!voice.inVoiceChannel()) {
            channel.sendMessage("${Emoji.X} You are not in a voice channel.").queue()
            return
        }

        lavaLink.getLink(message.guild).disconnect()
        channel.sendMessage("${Emoji.WHITE_CHECK_MARK} Disconnected from `${voice.channel.name}`.").queue()
    }
}