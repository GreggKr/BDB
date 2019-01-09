package me.greggkr.bdb.commands.osu

import com.oopsjpeg.osu4j.GameMode
import com.oopsjpeg.osu4j.backend.EndpointUserBests
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.osu
import me.greggkr.bdb.osu.Osu
import me.greggkr.bdb.ppFormat
import me.greggkr.bdb.twoDecFormat
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import kotlin.math.sqrt

@CommandDescription(name = "spread", triggers = [
    "spread"
], description = "Displays the osu! pp spread of the user.")
class SpreadCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel
        val p = Osu.getUserArguments(message, args)

        val user = p.user ?: return
        val limit = Osu.getNumberArgument(p.params, 50, 10, 100)

        val best = osu.userBests.getAsQuery(EndpointUserBests.ArgumentsBuilder(user)
                .setMode(GameMode.STANDARD)
                .setLimit(limit)
                .build())
                .resolve()

        val sorted = best.asSequence().sortedByDescending { it.pp }.toList()

        val median = if (sorted.size % 2 == 0) {
            (sorted[sorted.size / 2].pp + sorted[(sorted.size / 2) - 1].pp) / 2
        } else {
            sorted[sorted.size / 2].pp
        }

        val mean = sorted.sumByDouble { it.pp.toDouble() }.div(sorted.size)
        val squared = sorted
                .asSequence()
                .map { Math.pow(it.pp - mean, 2.0) }
                .toList()
        val meanSquared = squared.sum().div(squared.size)
        val standardDeviation = sqrt(meanSquared)

        val skew = (6 * (mean - median)) / standardDeviation

        channel.sendMessage(EmbedBuilder()
                .setColor(data.getColor(guild))
                .setTitle("PP Spread for $user")
                .setDescription("Top play: [${ppFormat.format(sorted[0].pp)}](https://osu.ppy.sh/b/${sorted[0].beatmapID})\n" +
                        "${limit}th play: [${ppFormat.format(sorted[limit - 1]?.pp)
                                ?: "None"}](https://osu.ppy.sh/b/${sorted[limit - 1]?.beatmapID
                                ?: 0})\n\n" +

                        "Mean: ${ppFormat.format(mean)}\n" +
                        "Median: ${ppFormat.format(median)}\n\n" +

                        "Range: ${ppFormat.format(sorted[0].pp - sorted[limit - 1].pp)}\n" +
                        "Standard Deviation: ${twoDecFormat.format(standardDeviation)}\n" +
                        "Skew (coeff 6): ${twoDecFormat.format(skew)}")
                .build())
                .queue()
    }
}