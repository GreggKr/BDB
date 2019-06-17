package me.greggkr.bdb.commands.osu

import com.oopsjpeg.osu4j.GameMode
import com.oopsjpeg.osu4j.backend.EndpointUserBests
import com.oopsjpeg.osu4j.backend.EndpointUsers
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.osu
import me.greggkr.bdb.util.Emoji
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "topplays", triggers = [
    "topplays"
], description = "Displays top osu! plays for people in your guild.")
class TopPlaysCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

//        val usernames = message.guild.members
//                .asSequence()
//                .map { it.user }
//                .mapNotNull { data.getOsuUser(it) }
//                .filter { userExists(it) }
//                .toList()

        val usernames = data.getAllOsuUsers(guild)
                ?.map { it.value }
                ?.filter { userExists(it) }
                ?.toList() ?: emptyList()

        if (usernames.isEmpty()) {
            channel.sendMessage("${Emoji.X} No users in this guild have osu! users attached to their account.").queue()
            return
        }

        val playsMessage = channel.sendMessage("Calculating top plays...").complete()

        val topPlays = usernames
                .asSequence()
                .map {
                    osu.userBests.getAsQuery(EndpointUserBests.ArgumentsBuilder(it)
                            .setMode(GameMode.STANDARD)
                            .setLimit(1)
                            .build())
                            .resolve()
                }.map { it[0] }
                .sortedByDescending { it.pp }.toList()


        val embed = EmbedBuilder()
                .setColor(data.getColor(guild))
                .setTitle("Top plays in ${guild.name}")
        for (play in topPlays) {
            val user = play.user.get()
            embed
                    .appendDescription("[${user.username}](https://osu.ppy.sh/users/${user.id}) ")
                    .appendDescription("(#${user.rank}): ")
                    .appendDescription("[${play.pp}pp](https://osu.ppy.sh/b/${play.beatmap.get().id})\n")
        }
        playsMessage.delete().queue()
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