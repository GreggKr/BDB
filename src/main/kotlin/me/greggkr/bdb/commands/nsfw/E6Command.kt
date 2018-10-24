package me.greggkr.bdb.commands.nsfw

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandAttribute
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.e6.E621
import me.greggkr.bdb.util.Emoji
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "e6", triggers = [
    "e6", "e621"
], attributes = [
    CommandAttribute(key = "nsfw")
], description = " no... dont")
class E6Command : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        val img = E621.getImage(if (args.isEmpty()) null else args)

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