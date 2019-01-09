package me.greggkr.bdb.commands.osu

import com.oopsjpeg.osu4j.GameMode
import com.oopsjpeg.osu4j.OsuScore
import com.oopsjpeg.osu4j.backend.EndpointUserBests
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.*
import me.greggkr.bdb.analysis.Analytics
import me.greggkr.bdb.analysis.analyse
import me.greggkr.bdb.osu.Osu
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageEmbed

data class ScoreInfo(val pp: Analytics,
                     val complete: Boolean,
                     val score: OsuScore
)

@CommandDescription(name = "recentbest", triggers = [
    "recentbest"
], description = "Displays the most recent plays from a users top 100.")
class RecentBestCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel
        val p = Osu.getUserArguments(message, args)

        val user = p.user ?: return
        val amount = Osu.getNumberArgument(p.params, 1, 1, 10)

        val best = osu.userBests.getAsQuery(EndpointUserBests.ArgumentsBuilder(user)
                .setMode(GameMode.STANDARD)
                .setLimit(100)
                .build())
                .resolve()

        best.sortByDescending { it.date }

        val recentBest = if (best.size > amount) {
            best.subList(0, amount)
        } else {
            best
        }

        val fields = recentBest
                .map { ScoreInfo(analyse(it.beatmapID, it.maxCombo, it.hit300, it.hit100, it.hit50, it.misses, it.enabledMods), !it.rank.contains("F"), it)}
                .filter { it.complete }
                .map {
                    val map = it.score.beatmap.get()
                    val rank = Osu.prettyRank(it.score.rank)
                    val comboInfo = "${it.score.maxCombo}x/${map.maxCombo}x"
                    val hitInfo = "${it.score.hit300}/${it.score.hit100}/${it.score.hit50}/${it.score.misses}"
                    val playTitle = Osu.playTitle(it.score)

                    val title = "$rank | $playTitle"
                    val content = "${ppFormat.format(it.pp.performance)} | ${accuracyFormat.format(it.pp.accuracy)}, $comboInfo, $hitInfo"

                    MessageEmbed.Field(title, content, false)
                }

        val builder = EmbedBuilder()
                .setColor(data.getColor(guild))
                .setTitle("Best recent plays for $user")

        fields.forEach { builder.addField(it) }

        channel.sendMessage(builder.build()).queue()
    }
}
