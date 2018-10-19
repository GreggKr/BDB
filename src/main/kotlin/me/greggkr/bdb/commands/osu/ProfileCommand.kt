package me.greggkr.bdb.commands.osu

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "profile", triggers = [
    "profile"
], description = "Displays osu profile for a user.")
class ProfileCommand : Command {
    override fun execute(message: Message, args: String) {
        
    }
}