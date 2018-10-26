package me.greggkr.bdb.commands.owner

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandAttribute
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.heroku.Heroku
import me.greggkr.bdb.util.Emoji
import me.greggkr.bdb.util.addInlineField
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "heroku", triggers = [
    "heroku"
], attributes = [
    CommandAttribute(key = "botOwnerOnly")
], description = "Manages Heroku stuff.")
class HerokuCommand : Command {
    override fun execute(message: Message, args: String) {
        val channel = message.channel

        if (args.isEmpty()) return
        val a = args.split(Regex("\\s+"))

        when (a[0].toLowerCase()) {
            "list" -> {
                val dynos = Heroku.listDynos(a[1])
                if (dynos == null || dynos.isEmpty()) {
                    channel.sendMessage("${Emoji.X} No dynos found.").queue()
                    return
                }

                val embed = EmbedBuilder()
                        .setColor(data.getColor(message.guild))

                for (dyno in dynos) {
                    embed.addInlineField(dyno.name, "Release: ${dyno.release.version}\n" +
                            "State: ${dyno.state}\n" +
                            "Command: ${dyno.command}\n" +
                            "Type: ${dyno.type}"
                    )
                }

                channel.sendMessage(embed.build()).queue()
                return
            }

            "restart" -> {
                val restarted = Heroku.restartDyno(a[1], a[2])
                if (restarted == null) {
                    channel.sendMessage("${Emoji.X} Could not find app/dyno.").queue()
                    return
                }

                if (!restarted) {
                    channel.sendMessage("${Emoji.X} Failed to restart dyno.").queue()
                    return
                }
                return
            }

            "stop" -> {
                val stopped = Heroku.stopDyno(a[1], a[2])
                if (stopped == null) {
                    channel.sendMessage("${Emoji.X} Could not find app/dyno.").queue()
                    return
                }

                if (!stopped) {
                    channel.sendMessage("${Emoji.X} Failed to stop dyno.").queue()
                    return
                }
                return
            }
        }
    }
}