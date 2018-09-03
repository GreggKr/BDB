package me.greggkr.bdb.commands.info

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.util.Emoji
import me.greggkr.bdb.util.Twitch
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "twitch", triggers = [
    "twitch"
], description = "Basic Twitch features")
class TwitchCommand : Command {
    override fun execute(message: Message, args: String) {
        val channel = message.channel

        if (args.isEmpty()) {
            channel.sendMessage("${Emoji.X} currently unsupported and im too lazy to write the help for this rn").queue()
            return
        }

        channel.sendMessage(EmbedBuilder()
                .setDescription("that user is ${if (Twitch.isStreaming(args) == true) "" else "not "} streaming")
                .build())
                .queue()
    }
}