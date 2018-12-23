package me.greggkr.bdb.nra

import com.jagrosh.jdautilities.commons.utils.FinderUtil
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandAttribute
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import net.dv8tion.jda.core.entities.Message

const val RESTRICTED_ROLE_ID = 514048223548014592L

@CommandDescription(name = "restrict", triggers = [
    "restrict"
], attributes = [
    CommandAttribute(key = "nra")
])
class RestrictCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        if (args.isBlank()) {
            channel.sendMessage("Correct usage: ${data.getPrefix(guild)}restrict <user(s)>").queue()
            return
        }

        val users = FinderUtil.findUsers(args, message.jda)

        if (users.isEmpty()) {
            // use id
            val id = args.toLong()
            if (data.isNRARestricted(id)) {
                data.nraUnrestrictUser(id)
                channel.sendMessage("Unrestricted `$id`.").queue()
            } else {
                data.nraRestrictUser(id)
                channel.sendMessage("Restricted `$id`.").queue()
            }
            data.nraRestrictUser(args.toLong())
            return
        }

        val controller = guild.controller
        val user = users[0]

        if (data.isNRARestricted(user.idLong)) {
            controller.removeRolesFromMember(guild.getMember(user), guild.getRoleById(RESTRICTED_ROLE_ID)).queue()
            data.nraUnrestrictUser(user.idLong)

            channel.sendMessage("Unrestricted `${user.name}#${user.discriminator}`.").queue()
        } else {
            controller.addRolesToMember(guild.getMember(user), guild.getRoleById(RESTRICTED_ROLE_ID)).queue()
            data.nraRestrictUser(user.idLong)

            channel.sendMessage("Restricted `${user.name}#${user.discriminator}`.").queue()
        }
    }
}


