package me.greggkr.bdb.commands.osu

import com.oopsjpeg.osu4j.GameMode
import com.oopsjpeg.osu4j.backend.EndpointUserBests
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.osu
import me.greggkr.bdb.osu.Osu
import me.greggkr.bdb.percentFormat
import me.greggkr.bdb.ppFormat
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
        val p = Osu.getUserArguments(message, args)

        val user = p.user ?: return
        val limit = Osu.getNumberArgument(p.params, 10, 1, 100, 0)

        val best = osu.userBests.getAsQuery(EndpointUserBests.ArgumentsBuilder(user)
                .setMode(GameMode.STANDARD)
                .setLimit(limit)
                .build())
                .resolve()


        val pps = best.map { it.pp }
        var weightedPP = 0.0

        for (i in 0 until pps.size) {
            weightedPP += (pps[i] * (Math.pow(.95, i.toDouble())))
        }

        val userPP = best[0].user.get().ppRaw

        channel.sendMessage(EmbedBuilder()
                .setColor(data.getColor(guild))
                .setTitle("PP Composition for $user ($limit plays)")
                .setDescription("That user's top $limit plays make up ${percentFormat.format(weightedPP / userPP)} of their total pp.")
                .addInlineField("PP", "Top $limit: ${ppFormat.format(weightedPP)}\n" +
                        "Total PP: ${ppFormat.format(userPP)}")
                .build())
                .queue()
    }
}