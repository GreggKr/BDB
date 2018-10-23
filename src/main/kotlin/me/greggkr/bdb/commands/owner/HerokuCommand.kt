package me.greggkr.bdb.commands.owner

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandAttribute
import me.diax.comportment.jdacommand.CommandDescription
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "heroku", triggers = [
    "heroku"
], attributes = [
    CommandAttribute(key = "botOwnerOnly")
], description = "Manages Heroku stuff.")
class HerokuCommand : Command {
    override fun execute(message: Message, args: String) {

    }
}