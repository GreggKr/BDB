package me.greggkr.bdb.commands.info

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "about", triggers = [
    "about"
], description = "Displays info about the bot.")
class AboutCommand : Command {
    override fun execute(message: Message, args: String) {
        message.channel.sendMessage(EmbedBuilder()
                .setDescription("Made for the osu! team Baguetteophobia")
                .setFooter("Created by Grzegorz#6088", null)
                .build())
                .queue()
    }
}