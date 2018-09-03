package me.greggkr.bdb.commands.info

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.util.Emoji
import me.greggkr.bdb.util.Hastebin
import me.greggkr.bdb.util.twitch.Twitch
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import java.util.*

@CommandDescription(name = "twitch", triggers = [
    "twitch"
], description = "Basic Twitch features")
class TwitchCommand : Command {
    override fun execute(message: Message, args: String) {
        val channel = message.channel

        if (args.isEmpty()) {
            channel.sendMessage("${Emoji.X} currently unsupported and im too lazy to write the help for this rn").queue()
            return
        }

        val a = args.split(Regex(","))

        val url = if (a.size < 2) {
            Hastebin.hastebin(Twitch.getUser(a[0]).toString())
        } else {
            Hastebin.hastebin(Arrays.deepToString(Twitch.getUsers(a)))
        }

        channel.sendMessage(url).queue()
    }
}