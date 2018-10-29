package me.greggkr.bdb.commands.info

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "dabby", triggers = [
    "dabby"
], description = "send dabby")
class DabbyCommand : Command {
    override fun execute(message: Message, args: String) {
        message.channel.sendMessage(EmbedBuilder()
                .setImage("https://cdn.discordapp.com/attachments/204464836283858945/506264841548201985/dabby.png")
                .build())
                .queue()
    }
}