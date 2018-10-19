package me.greggkr.bdb.commands.osu

import com.oopsjpeg.osu4j.GameMode
import com.oopsjpeg.osu4j.backend.EndpointUserBests
import com.oopsjpeg.osu4j.backend.EndpointUsers
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.osu
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import kotlin.math.roundToInt

@CommandDescription(name = "topacc", triggers = [
    "topacc"
], description = "Displays top osu! accuracies for people in your guild.")
class TopAccCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        val usernames = message.guild.members
                .asSequence()
                .map { it.user }
                .mapNotNull { data.getOsuUser(it) }
                .filter { userExists(it) }
                .toList()

        val users = usernames
                .asSequence()
                .map {
                    osu.users.getAsQuery(EndpointUsers.ArgumentsBuilder(it)
                            .setMode(GameMode.STANDARD)
                            .build())
                            .resolve()
                }
                .sortedByDescending { it.accuracy }

        val embed = EmbedBuilder()
                .setColor(data.getColor(guild))
                .setTitle("Top accuracies in ${guild.name}")

        for (user in users) {
            embed
                    .appendDescription("[${user.username}](https://osu.ppy.sh/users/${user.id}) ")
                    .appendDescription("(#${user.rank}): ")
                    .appendDescription("${user.accuracy}%\n")
        }
        channel.sendMessage(embed.build()).queue()
    }

    private fun userExists(username: String): Boolean {
        return try {
            osu.users.getAsQuery(EndpointUsers.ArgumentsBuilder(username)
                    .build())
                    .resolve() != null
        } catch (e: Exception) {
            false
        }
    }
}