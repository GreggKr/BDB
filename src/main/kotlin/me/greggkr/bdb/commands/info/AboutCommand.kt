package me.greggkr.bdb.commands.info

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "about", triggers = [
    "about"
], description = "Displays info about the bot.")
class AboutCommand : Command {
    override fun execute(message: Message, args: String) {
        message.channel.sendMessage(EmbedBuilder()
                .setColor(data.getColor(message.guild))
                .setDescription("Made for the osu! team Baguetteophobia\n" +
                        "I should really update this...")
                .setFooter("Created by Grzegorz", null)
                .build())
                .queue()
    }
}