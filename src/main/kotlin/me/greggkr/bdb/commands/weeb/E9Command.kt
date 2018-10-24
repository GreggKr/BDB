package me.greggkr.bdb.commands.weeb

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.e9.E926
import me.greggkr.bdb.util.Emoji
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "e9", triggers = [
    "e9", "e926"
], description = " no... dont (but not porn)")
class E9Command : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        val img = E926.getImage(if (args.isEmpty()) null else args)

        if (img == null) {
            channel.sendMessage("${Emoji.WHITE_CHECK_MARK} ").queue()
        }

        channel.sendMessage(EmbedBuilder()
                .setColor(data.getColor(guild))
                .setImage(img)
                .build())
                .queue()
    }
}