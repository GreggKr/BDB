package me.greggkr.bdb.commands.osu

import com.oopsjpeg.osu4j.GameMode
import com.oopsjpeg.osu4j.backend.EndpointUserBests
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.osu
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "composition", triggers = [
    "composition"
], description = "Gets your composition score.")
class CompositionCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        val best = osu.userBests.getAsQuery(EndpointUserBests.ArgumentsBuilder(args)
                .setMode(GameMode.STANDARD)
                .build())
                .resolve()

        val topPP = best.sumByDouble { it.pp.toDouble() }
        val userPP = best[0].user.get().ppRaw

        channel.sendMessage("your top 10 plays make up ${(topPP / userPP) * 100}% of your total pp ($topPP / $userPP)").queue()
    }
}