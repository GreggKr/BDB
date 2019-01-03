package me.greggkr.bdb.commands.osu

import com.oopsjpeg.osu4j.GameMode
import com.oopsjpeg.osu4j.backend.EndpointUsers
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.osu
import me.greggkr.bdb.util.Emoji
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "topacc", triggers = [
    "topacc"
], description = "Displays top osu! accuracies for people in your guild.")
class TopAccCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

//        val usernames = message.guild.members
//                .asSequence()
//                .map { it.user }
//                .mapNotNull { data.getOsuUser(guild, it) }
//                .filter { userExists(it) }
//                .toList()

        val usernames = data.getAllOsuUsers(guild)
                ?.map { it.value }
                ?.toList() ?: emptyList()

        if (usernames.isEmpty()) {
            channel.sendMessage("${Emoji.X} No users in this guild have osu! users attached to their account.").queue()
            return
        }

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
}
