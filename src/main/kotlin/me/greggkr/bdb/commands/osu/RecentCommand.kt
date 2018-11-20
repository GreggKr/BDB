package me.greggkr.bdb.commands.osu

import com.oopsjpeg.osu4j.GameMode
import com.oopsjpeg.osu4j.backend.EndpointUserRecents
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.osu
import me.greggkr.bdb.util.Emoji
import me.greggkr.bdb.util.addInlineField
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
        var user = if (!message.mentionedUsers.isEmpty()) {
            data.getOsuUser(message.mentionedUsers[0])
        } else {
            if (a.isNullOrEmpty() || a[0].isEmpty()) {
                println("empty")
                data.getOsuUser(message.author)
            } else {
                a[0]
            }
        }

        if (user == null) {
            user = data.getOsuUser(message.author)
            if (user == null) {
                channel.sendMessage("${Emoji.X} You must supply a valid user. Either the person you mentioned or you do not have a linked user. Use ${data.getPrefix(guild)}user <username>.").queue()
                return
            }
        }
        println(user)

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

        val mods = if (play.enabledMods.isEmpty()) {
            "No Mod"
        } else {
            "+" + play.enabledMods.joinToString(separator = "") { it.name }
        }

        val bitwiseMods = 0
        for (mod in play.enabledMods) {
            bitwiseMods or mod.bit.toInt()
        }
//        val pp = analyse(Score(
//                map.id,
//                play?.maxCombo ?: 0,
//                play?.hit300 ?: 0,
//                play.hit100,
//                play.hit50,
//                play.misses,
//                bitwiseMods
//        ))

        channel.sendMessage(EmbedBuilder()
                .setColor(data.getColor(guild))
                .setTitle("${map.title} [${map.version}] $mods [${map.difficulty}]")
                .addInlineField("Rank", play.rank)
                .addInlineField("max_combo/300s/100s/50s/Xs", "${map.maxCombo}/${play.hit300}/${play.hit100}/${play.hit50}/${play.misses}")
//                .addInlineField("Calculated PP", "Aim: ${pp?.aim}\nSpeed: ${pp?.speed}\nTotal: ${pp?.total}")
                .setTimestamp(play.date.toOffsetDateTime())
                .build())
                .queue()
    }
}