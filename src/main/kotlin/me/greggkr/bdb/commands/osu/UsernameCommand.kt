package me.greggkr.bdb.commands.osu

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "username", triggers = [
    "username", "un"
], description = "Gets/Sets your linked osu! user.")
class UsernameCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val user = message.author
        val channel = message.channel

        if (args.isBlank()) {
            val current = data.getOsuUser(guild, user)
            if (current == null) {
                channel.sendMessage("Use `${data.getPrefix(guild)}user <osu! username>` to set your username.").queue()
            } else {
                channel.sendMessage("Your current osu! username is set to `$current`.").queue()
            }
        } else {
            data.setOsuUser(guild, user, args)
            channel.sendMessage("Set your osu! username to `$args`.").queue()
        }
    }
}