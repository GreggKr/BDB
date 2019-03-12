package me.greggkr.bdb.commands.osu

import com.oopsjpeg.osu4j.GameMode
import com.oopsjpeg.osu4j.backend.EndpointUsers
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.osu
import me.greggkr.bdb.osu.Osu
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "user", triggers = [
    "user", "u"
], description = "Allows you access stats of a user.")
class UserCommand : Command {
    override fun execute(message: Message, args: String) {
        val channel = message.channel
        val p = Osu.getUserArguments(message, args).user

        if (p == null) {
            channel.sendMessage("That user was not found.").queue()
            return
        }

        val user = osu.users.getAsQuery(EndpointUsers.ArgumentsBuilder(p)
                .setMode(GameMode.STANDARD)
                .build())
                .resolve()

        channel.sendMessage(EmbedBuilder()
                .setColor(data.getColor(message.guild))
                .setDescription(user.url.toString())
                .build())
                .queue()
    }
}