package me.greggkr.bdb.commands.osu

import com.oopsjpeg.osu4j.GameMode
import com.oopsjpeg.osu4j.backend.EndpointUserBests
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.accuracyFormat
import me.greggkr.bdb.analysis.analyse
import me.greggkr.bdb.data
import me.greggkr.bdb.osu
import me.greggkr.bdb.ppFormat
import me.greggkr.bdb.util.Emoji
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
            channel.sendMessage("${Emoji.X} Correct usage: ${data.getPrefix(guild)}profile <username>").queue()
            return
        }

        val a = args.split(Regex("\\s+\\|\\s+"))

        val best = osu.userBests.getAsQuery(EndpointUserBests.ArgumentsBuilder(a[0])
                .setMode(GameMode.STANDARD)
                .setLimit(10)
                .build())
                .resolve()

        val pps = best.map { it.pp }
        val ranks = best.map { it.rank }

        val accuracies = best.map {
            val bitwiseMods = 0
            for (mod in it.enabledMods) {
                bitwiseMods or mod.bit.toInt()
            }
            analyse(it.beatmapID, it.hit100, it.hit50, it.misses, bitwiseMods).accuracy
        }

        val user = best[0].user.get()
        val userPp = user.ppRaw
        val userAcc = user.accuracy

        var content = "Username: [${user.username}](https://osu.ppy.sh/users/${user.id})\n" +
                "Acc: ${accuracyFormat.format(userAcc)}%\n" +
                "Total pp: ${ppFormat.format(userPp)}\n" +
                "\n" +
                "Top plays:\n"

        for (i in 0 until pps.size) {
            val beatmap = best[i].beatmap.get()
            val beatmapInfo = "${beatmap.title} (${beatmap.creatorName})"

            content += "- ${ppFormat.format(pps[i])} (${ranks[i]} | ${accuracyFormat.format(accuracies[i])}) | [" + beatmapInfo + "](https://osu.ppy.sh/b/${beatmap.id})\n"
        }

        channel.sendMessage(EmbedBuilder()
                .setColor(data.getColor(guild))
                .setTitle("Profile for ${a[0]}")
                .setDescription(content)
                .build())
                .queue()
    }
}
