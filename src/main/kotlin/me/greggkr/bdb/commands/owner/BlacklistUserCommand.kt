package me.greggkr.bdb.commands.owner

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandAttribute
import me.diax.comportment.jdacommand.CommandDescription
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "blacklist", triggers = [
    "blacklist", "blacklistuser"
], attributes = [
    CommandAttribute(key = "")
], description = " Allows you to blacklist a user from using the bot.")
class BlacklistUserCommand : Command {
    override fun execute(p0: Message?, p1: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}