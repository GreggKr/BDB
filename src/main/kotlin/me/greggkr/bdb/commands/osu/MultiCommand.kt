package me.greggkr.bdb.commands.osu

import com.oopsjpeg.osu4j.GameMod
import com.oopsjpeg.osu4j.OsuMatch
import com.oopsjpeg.osu4j.backend.EndpointMatches
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.analysis.calculateDifficulty
import me.greggkr.bdb.data
import me.greggkr.bdb.osu
import me.greggkr.bdb.starFormat
import me.greggkr.bdb.util.Emoji
import me.greggkr.bdb.util.addInlineField
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import org.jetbrains.kotlin.utils.addToStdlib.sumByLong

@CommandDescription(name = "multi", triggers = [
    "multi"
], description = "Gives statistics for a multi match")
class MultiCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        if (args.isBlank()) {
            channel.sendMessage("${Emoji.X} Correct Usage: ${data.getPrefix(guild)}multi <multi link> [map]").queue()
            return
        }

        val a = args.split(Regex("\\s+"))
        if (a.isEmpty()) {
            channel.sendMessage("${Emoji.X} Correct Usage: ${data.getPrefix(guild)}multi <multi link> [map]").queue()
            return
        }

        val linkParts = a[0].split(Regex("/"))
        val id = linkParts[linkParts.size - 1].toIntOrNull()
        if (id == null) {
            channel.sendMessage("${Emoji.X} Correct Usage: ${data.getPrefix(guild)}multi <multi link> [map]").queue()
            return
        }

        val map = if (a.size < 2) {
            -1
        } else {
            a[1].toIntOrNull()
        }

        try {
            val match = osu.matches.getAsQuery(EndpointMatches.ArgumentsBuilder(id)
                    .build())
                    .resolve()

            val games = match.games ?: null
            if (map == null || map < 0 || games == null || games.size < map) {
                val count = games?.filter { !it.scores.isNullOrEmpty() }?.count() ?: 0

                channel.sendMessage(EmbedBuilder()
                        .setColor(data.getColor(guild))
                        .setTitle(match.name, "https://osu.ppy.sh/community/matches/${match.id}")
                        .addInlineField("Maps", count)
                        .setTimestamp(match.startTime)
                        .build())
                        .queue()
            } else {
                val game = games[map - 1]
                if (game == null) {
                    channel.sendMessage("${Emoji.X} That map does not exist.").queue()
                    return
                }

                val scores = game.scores.sortedByDescending { it.score }

                val mvp = scores[0]

                val modStr = if (game.mods.isNullOrEmpty()) "FM" else GameMod.toShortName(game.mods)

                val beatmap = game.beatmap.get()

                val diff = calculateDifficulty(beatmap.id, if (modStr == "FM") "" else modStr)
                channel.sendMessage("```${beatmap.artist} - ${beatmap.title} [${beatmap.version}] - ${beatmap.creatorName} +$modStr\n" +
                        "(${starFormat.format(diff.stars)}STR, ${beatmap.hitLength}DRT, ${beatmap.bpm}BPM, CS${beatmap.size}, AR${beatmap.approach}, HP${beatmap.drain}, OD${beatmap.overall})\n\n" +

                        "MVP: ${mvp.user.get().username} - ${mvp.score}\n\n" +
                        if (OsuMatch.TeamType.isTeamMode(game.teamType)) {
                            val red = scores.filter { it.team == 2 }
                            val blue = scores.filter { it.team == 1 }
                            val redTotal = red.sumByLong { it.score.toLong() }
                            val blueTotal = blue.sumByLong { it.score.toLong() }

                            val scoreSb = StringBuilder()
                            if (blueTotal > redTotal) {
                                scoreSb.append("Blue:\n")
                                blue.forEach {
                                    scoreSb
                                            .append(it.score)
                                            .append(": ")
                                            .append(it.user.get().username)
                                            .append("\n")
                                }

                                scoreSb.append("\nRed: \n")
                                red.forEach {
                                    scoreSb
                                            .append(it.score)
                                            .append(": ")
                                            .append(it.user.get().username)
                                            .append("\n")
                                }
                            } else {
                                scoreSb.append("Red: \n")
                                red.forEach {
                                    scoreSb
                                            .append(it.score)
                                            .append(": ")
                                            .append(it.user.get().username)
                                            .append("\n")
                                }

                                scoreSb.append("\nBlue:\n")
                                blue.forEach {
                                    scoreSb
                                            .append(it.score)
                                            .append(": ")
                                            .append(it.user.get().username)
                                            .append("\n")
                                }
                            }

                            if (redTotal > blueTotal) {
                                "$redTotal: Red < (+${redTotal - blueTotal})\n" +
                                        "$blueTotal: Blue\n\n$scoreSb"
                            } else {
                                "$blueTotal: Blue ${if (blueTotal > redTotal) "< (+${blueTotal - redTotal})" else ""}\n" +
                                        "$redTotal: Red\n\n$scoreSb"
                            }
                        } else {
                            val sb = StringBuilder("Scores:\n")
                            if (scores.size == 2) {
                                val score0 = scores[0].score
                                val score1 = scores[1].score
                                sb
                                        .append(score0)
                                        .append(": ")
                                        .append(scores[0].user.get().username)
                                        .append(" < (+${score0 - score1})\n")
                                        .append(scores[1].score)
                                        .append(": ")
                                        .append(scores[1].user.get().username)
                                        .append("\n")
                            } else {
                                scores.forEach {
                                    sb
                                            .append(it.score)
                                            .append(": ")
                                            .append(it.user.get().username)
                                            .append("\n")
                                }
                            }
                        } + "```").queue()
            }
        } catch (e: Exception) {
            channel.sendMessage("${Emoji.X} Incorrect multi link.").queue()
            e.printStackTrace()
        }
    }
}
