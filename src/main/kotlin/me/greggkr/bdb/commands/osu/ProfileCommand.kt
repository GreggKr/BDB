package me.greggkr.bdb.commands.osu

import com.oopsjpeg.osu4j.GameMode
import com.oopsjpeg.osu4j.backend.EndpointUserBests
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.osu
import me.greggkr.bdb.util.Emoji
import me.greggkr.bdb.util.addInlineField
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "profile", triggers = [
    "profile"
], description = "Displays osu profile for a user.")
class ProfileCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        if (args.isEmpty()) {
            channel.sendMessage("${Emoji.X} Correct usage: ${data.getPrefix(guild)}composition <username> [amt]").queue()
            return
        }

        val a = args.split(Regex("\\s+\\|\\s+"))

        val best = osu.userBests.getAsQuery(EndpointUserBests.ArgumentsBuilder(args)
                .setMode(GameMode.STANDARD)
                .setLimit(10)
                .build())
                .resolve()

        val pps = best.map { it.pp }
        val weightedPps = mutableListOf<Double>()

        for (i in 0 until pps.size) {
            weightedPps.add(pps[i] * Math.pow(.95, i.toDouble()))    
        }

        val user = best[0].user.get()
        val userPp = user.ppRaw
        val userAcc = user.accuracy

        var content = "Username: [${user.username}](https://osu.ppy.sh/users/${user.id})\n" +
                      "Acc: $userAcc\n" +
                      "Total pp: $userPp\n" +
                      "\n" +
                      "Top plays:\n"

        for (i in 0 until pps.size) {
            val rawPp = pps[i]
            val pp = weightedPps[i]
            val percent = "%.2f".format(100.0 * Math.pow(.95, i.toDouble()))

            val beatmap = best[i].beatmap.get()
            val beatmapInfo = "${beatmap.title} (${beatmap.creatorName})"

            content += "${ i + 1 } - $rawPp ($pp / " + percent + "%) - [" + beatmapInfo + "](https://osu.ppy.sh/b/${beatmap.id})\n"
        }

        channel.sendMessage(EmbedBuilder()
                .setColor(data.getColor(guild))
                .setTitle("Profile for ${a[0]}")
                .setDescription(content)
                .build())
                .queue()
    }
}
