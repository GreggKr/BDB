package me.greggkr.bdb.commands.osu

import com.oopsjpeg.osu4j.GameMode
import com.oopsjpeg.osu4j.backend.EndpointUserRecents
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.*
import me.greggkr.bdb.analysis.analyse
import me.greggkr.bdb.osu.Osu
import me.greggkr.bdb.util.Emoji
import me.greggkr.bdb.util.gameModeFromName
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "recent", triggers = [
    "recent"
], description = "Displays the most recent play for a user.")
class RecentCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        val a = args.split(Regex("\\s+\\|\\s+"))
        val user = Osu.getOsuUser(message, a) ?: return

        val recent = osu.userRecents.getAsQuery(EndpointUserRecents.ArgumentsBuilder(user)
                .setLimit(1)
                .setMode(if (a.size < 2) GameMode.STANDARD else gameModeFromName(a[1]))
                .build())
                .resolve()

        if (recent == null || recent.isEmpty()) {
            channel.sendMessage("${Emoji.X} That user does not have a recent play.").queue()
            return
        }

        val play = recent[0]
        val map = play.beatmap.get()

        val rank = Osu.prettyRank(play.rank)
        val comboInfo = "${play.maxCombo}x/${map.maxCombo}x"
        val mods = Osu.prettyMods(play.enabledMods)

        val pp = analyse(map.id, play.maxCombo, play.hit300, play.hit100, play.hit50, play.misses, play.enabledMods)
        var description = ""

        if (rank.contains("F")) {
            val mapCompletion = pp.mapCompletion
            description = "Map completion: ${percentFormat.format(mapCompletion)}"
        }

        channel.sendMessage(EmbedBuilder()
                .setColor(data.getColor(guild))
                .setTitle("${map.title} [${map.version}] $mods [${starFormat.format(map.difficulty)}*]")
                .addField("Rank, Combo, Acc", rank + ", ${comboInfo}, ${accuracyFormat.format(pp.accuracy)}", false)
                .addField("PP", ppFormat.format(pp.performance), false)
                .addField("PP Breakdown", "Aim: ${ppFormat.format(pp.aimPerformance)}\n" +
                        "Speed: ${ppFormat.format(pp.speedPerformance)}\n" +
                        "Acc: ${ppFormat.format(pp.accuracyPerformance)}", false)
                .addField("300/100/50/miss", "${play.hit300}/${play.hit100}/${play.hit50}/${play.misses}", false)
                .setDescription(description)
                .setTimestamp(play.date.toOffsetDateTime())
                .build())
                .queue()
    }
}
