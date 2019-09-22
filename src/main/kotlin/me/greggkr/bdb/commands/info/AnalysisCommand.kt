package me.greggkr.bdb.commands.info

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.handler
import me.greggkr.bdb.util.analysisData
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "analysis", triggers = [
    "analysis"
], description = "Displays analysis information about a specific command.")
class AnalysisCommand : Command {
    override fun execute(message: Message, args: String) {
        val a = args.split(Regex("\\s+"))

        if (a.isEmpty()) {
            message.channel.sendMessage("Correct Usage: ${data.getPrefix(message.guild)}analysis <command>").queue()
            return
        }

        val command = handler.findCommand(a[0])
        if (command == null) {
            message.channel.sendMessage("Could not find that command.").queue()
            return
        }

        val duration = analysisData[command]

        if (duration == null) {
            message.channel.sendMessage("Internal error. Duration does not exist.").queue()
            return
        }

        val average = duration.stream()
                .map { it.duration.toMillis() }
                .mapToLong { it }
                .average()

        message.channel.sendMessage("That command takes an average of ${average.orElse(0.0).toInt()}ms over ${duration.size} runs to execute.").queue()
    }
}