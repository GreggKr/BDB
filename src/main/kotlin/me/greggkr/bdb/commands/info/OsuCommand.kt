package me.greggkr.bdb.commands.info

import club.minnced.kjda.builders.embed
import com.oopsjpeg.osu4j.GameMode
import com.oopsjpeg.osu4j.backend.EndpointUsers
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.osu
import me.greggkr.bdb.util.Emoji
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel

@CommandDescription(name = "osu", triggers = [
    "osu", "o"
], description = "Command for getting osu! information.")
class OsuCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel
        val prefix = data.getPrefix(guild)

        if (args.isEmpty()) {
            sendHelp(channel)
            return
        }

        val a = args.split(Regex("\\s+"))
        if (a.isEmpty()) {
            sendHelp(channel)
            return
        }

        when (a[0].toLowerCase()) {
            "user", "u" -> {
                if (a.size < 2) { // 0 -> user, 1 -> the user
                    channel.sendMessage("${Emoji.X} Correct Usage: ${prefix}osu u <user>").queue()
                    return
                }

                val name = a[1]
                val mode = a.getOrNull(2)
                val parsedMode = parseMode(mode)

                val user = osu.users.query(EndpointUsers.ArgumentsBuilder(name)
                        .setMode(parsedMode)
                        .build())

                if (user == null) {
                    channel.sendMessage("${Emoji.X} User not found").queue()
                    return
                }

                channel.sendMessage(embed {
                    title { "${parsedMode.name} info for $name" }
                    description {
                        "Name: ${user.username}\n" +
                                "Acc: ${user.accuracy}%\n" +
                                "PP: ${user.ppRaw}\n" +
                                "Rank (Global/Country): #${user.rank}/#${user.countryRank}"
                    }
                }).queue()
            }
        }
    }

    private fun sendHelp(channel: MessageChannel) {
        channel.sendMessage("owo still lazy").queue()
    }

    private fun parseMode(mode: Any?): GameMode {
        var m = (mode as String?)?.toIntOrNull() ?: 0
        if (m < 0 || m > 3) m = 0
        return GameMode.fromID(m)
    }
}