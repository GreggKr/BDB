package me.greggkr.bdb.commands.audio

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.lavaLink
import me.greggkr.bdb.util.Emoji
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "connect", triggers = [
    "connect"
], description = "Connects the bot to your voice channel.")
class ConnectCommand : Command {
    override fun execute(message: Message, args: String) {
        val channel = message.channel
        val voice = message.member.voiceState

        if (!voice.inVoiceChannel()) {
            channel.sendMessage("${Emoji.X} You are not in a voice channel.").queue()
            return
        }

        lavaLink.getLink(message.guild).connect(voice.channel)
        channel.sendMessage("${Emoji.WHITE_CHECK_MARK} Connected to `${voice.channel.name}`.").queue()
    }
}