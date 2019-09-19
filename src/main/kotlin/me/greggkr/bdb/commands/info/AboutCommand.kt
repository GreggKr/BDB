package me.greggkr.bdb.commands.info

import com.google.common.base.Stopwatch
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import java.util.concurrent.TimeUnit

@CommandDescription(name = "about", triggers = [
    "about"
], description = "Displays info about the bot.")
class AboutCommand : Command {
    override fun execute(message: Message, args: String) {
        val stopwatch = Stopwatch.createStarted()
        message.channel.sendMessage(EmbedBuilder()
                .setColor(data.getColor(message.guild))
                .setDescription("Made for the osu! team Baguetteophobia\n" +
                        "I should really update this...")
                .setFooter("Created by Grzegorz", null)
                .build())
                .queue()

        message.channel.sendMessage("Elapsed Time: ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms").queue()
    }
}