package me.greggkr.bdb.commands.info

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.util.CommandRegistry
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import java.lang.StringBuilder

@CommandDescription(name = "help", triggers = [
    "help"
], description = "Displays help")
class HelpCommand : Command {
    override fun execute(message: Message, args: String) {
        val sb = StringBuilder()
        for (cmd in CommandRegistry.commands) {
            sb.append(cmd.description.name).append("\n")
        }

        message.channel.sendMessage(EmbedBuilder()
                .setColor(data.getColor(message.guild))
                .setDescription(sb)
                .build())
                .queue()
    }
}