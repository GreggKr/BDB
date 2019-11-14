package me.greggkr.bdb.commands.info

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.util.addInlineField
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import java.time.format.DateTimeFormatter

@CommandDescription(name = "oldestmember", triggers = [
    "oldestmember"
], description = "Returns the oldest member on the discord server.")
class OldestMemberCommand : Command {
    override fun execute(msg: Message, args: String) {
        val guild = msg.guild
        msg.channel.sendMessage("Calculating oldest member. This might take a while.").queue()


        val members = guild.members
        var oldest = members[0]
        members.forEach {
            if (it.user.creationTime.isBefore(oldest.user.creationTime)) {
                oldest = it
            }
        }

        val joined = oldest.user.creationTime
        val formatted = joined.format(DateTimeFormatter.ofPattern("yyy-MM-dd hh:mm:ss"))

        msg.channel.sendMessage(EmbedBuilder()
                .addInlineField("Oldest Member", oldest.asMention)
                .addInlineField("Registered On", formatted)
                .build()).queue()
    }
}