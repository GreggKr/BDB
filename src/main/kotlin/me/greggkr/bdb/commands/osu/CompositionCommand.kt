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
import java.lang.StringBuilder
import kotlin.math.max

@CommandDescription(name = "composition", triggers = [
    "composition"
], description = "Gets your composition score.")
class CompositionCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        if (args.isEmpty()) {
            channel.sendMessage("${Emoji.X} Correct usage: ${data.getPrefix(guild)}composition <username> [amt]").queue()
            return
        }

        channel.history.retrievePast(100).queue { it ->
            it.forEach {
                ((it.delete()))
            }
        }

        val a = args.split(Regex("\\s+\\|\\s+"))

        val limit = if (a.size < 2) 10 else {
            var tmp = a[1].toIntOrNull() ?: 10
            if (tmp < 1) {
                tmp = 1
            }

            if (tmp > 100) {
                tmp = 100
            }

            tmp
        }

        val best = osu.userBests.getAsQuery(EndpointUserBests.ArgumentsBuilder(a[0])
                .setMode(GameMode.STANDARD)
                .setLimit(limit)
                .build())
                .resolve()


        val pps = best.map { it.pp }
        var weightedPP = 0.0

        for (i in 0 until pps.size) {
            weightedPP += (pps[i] * (Math.pow(.95, i.toDouble())))
        }

//        val topPP = weigh.sumByDouble { it.pp.toDouble() }

        // https://osu.ppy.sh/help/wiki/Performance_Points#Weightage_system
        // PP[n] * 0.95^(n-1)


        val userPP = best[0].user.get().ppRaw

        channel.sendMessage(EmbedBuilder()
                .setColor(data.getColor(guild))
                .setTitle("PP Composition for ${a[0]} ($limit plays)")
                .addInlineField("PP", "Top $limit: $weightedPP\n" +
                        "Total PP: $userPP")
                .setDescription("Your top $limit plays make up ${(weightedPP / userPP) * 100}% of your total pp.")
                .build())
                .queue()
    }
}