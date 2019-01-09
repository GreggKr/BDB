package me.greggkr.bdb.commands.osu

import com.oopsjpeg.osu4j.GameMode
import com.oopsjpeg.osu4j.backend.EndpointUserBests
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.accuracyFormat
import me.greggkr.bdb.analysis.getAcc
import me.greggkr.bdb.data
import me.greggkr.bdb.osu
import me.greggkr.bdb.osu.Osu
import me.greggkr.bdb.ppFormat
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "profile", triggers = [
    "profile"
], description = "Displays osu profile for a user.")
class ProfileCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel
        val p = Osu.getUserArguments(message, args)

        val inputUser = p.user ?: return

        val best = osu.userBests.getAsQuery(EndpointUserBests.ArgumentsBuilder(inputUser)
                .setMode(GameMode.STANDARD)
                .setLimit(10)
                .build())
                .resolve()

        val pps = best.map { it.pp }
        val ranks = best.map { Osu.prettyRank(it.rank) }
        val mods = best.map { Osu.prettyMods(it.enabledMods)}

        val accuracies = best.map {
            getAcc(it.hit300, it.hit100, it.hit50, it.misses)
        }

        val user = best[0].user.get()
        val userPp = user.ppRaw
        val userAcc = user.accuracy / 100.0

        var playList = ""

        for (i in 0 until pps.size) {
            val beatmap = best[i].beatmap.get()
            playList += "- ${ppFormat.format(pps[i])} | ${ranks[i]}, ${accuracyFormat.format(accuracies[i])}, [${beatmap.title} [${beatmap.version}]](https://osu.ppy.sh/b/${beatmap.id})${mods[i]}\n"
        }

        channel.sendMessage(EmbedBuilder()
                .setColor(data.getColor(guild))
                .addField("User", "[${user.username}](https://osu.ppy.sh/users/${user.id}) | ${accuracyFormat.format(userAcc)}, ${ppFormat.format(userPp)}", false)
                .addField("Score", "Ranked: ${user.rankedScore}, Total: ${user.totalScore}", false)
                .addField("Top Plays", playList, false)
                .build())
                .queue()
    }
}
