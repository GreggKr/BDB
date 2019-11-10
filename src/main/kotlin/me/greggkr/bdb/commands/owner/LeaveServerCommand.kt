package me.greggkr.bdb.commands.owner

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandAttribute
import me.diax.comportment.jdacommand.CommandDescription
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "leaveserver", triggers = [
    "leaveserver"
], attributes = [
    CommandAttribute(key = "botOwnerOnly"),
    CommandAttribute(key = "hideFromHelp")
], description = "Manages Heroku stuff.")
class LeaveServerCommand : Command {
    override fun execute(message: Message, args: String) {
        message.guild.leave().queue()
    }
}