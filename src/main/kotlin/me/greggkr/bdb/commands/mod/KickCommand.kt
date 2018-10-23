package me.greggkr.bdb.commands.mod

import com.jagrosh.jdautilities.commons.utils.FinderUtil
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandAttribute
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.util.Emoji
import me.greggkr.bdb.logging.Logger
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "kick", triggers = [
    "kick"
], attributes = [
    CommandAttribute(key = "modOnly")
], description = "Allows you to kick people.")
class KickCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        val a = args.split(Regex("\\s+\\|\\s+"))

        if (a[0].isEmpty()) {
            channel.sendMessage("${Emoji.X} Correct Usage: ${data.getPrefix(guild)}kick <user> | <reason>").queue()
            return
        }

        val self = message.guild.selfMember
        val member = message.member
        if (!self.hasPermission(Permission.KICK_MEMBERS)) {
            channel.sendMessage("${Emoji.X} I do not have permission to do this. I need the following permission: KICK_MEMBERS.").queue()
            return
        }

        if (!self.canInteract(member)) {
            channel.sendMessage("${Emoji.X} I cannot kick that person.").queue()
            return
        }

        val users = FinderUtil.findUsers(a[0], message.jda)
        if (users.isEmpty()) {
            channel.sendMessage("${Emoji.X} Failed to find user `$a`.").queue()
            return
        }

        val reason = if (a.size > 1) a[1] else "No reason specified."

        val user = users[0]
        guild.controller.kick(user.id, reason).queue({
            channel.sendMessage("${Emoji.WHITE_CHECK_MARK} Kicked ${user.asMention} for `$reason`.").queue()
            Logger.logMessage(guild, "Member Kicked", user.effectiveAvatarUrl, "Kicked by bot", "Kicked: ${user.asMention}\nModerator: ${message.author.asMention}")
        }, {
            channel.sendMessage("${Emoji.X} Failed to kick ${user.asMention}").queue()
        })
    }
}