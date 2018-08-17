package me.greggkr.bdb.handlers

import me.diax.comportment.jdacommand.CommandHandler
import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class CommandHandler(private val handler: CommandHandler) : ListenerAdapter() {
    override fun onMessageReceived(e: MessageReceivedEvent) {
        if (e.author.isBot || e.channelType != ChannelType.TEXT) return

        val channel = e.channel
        val msg = e.message
        val content = msg.contentRaw

        val prefix = "!" // TODO: replace with config value
        if (content == e.guild.selfMember.asMention) {
            channel.sendMessage("My prefix here is: `$prefix`.").queue()
            return
        }

        if (!content.startsWith(prefix)) return

        val args = content.split(Regex("\\s+"), 2)
        val trigger = args[0].substring(prefix.length)

        val cmd = handler.findCommand(trigger.toLowerCase()) ?: return

        handler.execute(cmd, e.message, if (args.size > 1) args[1] else "")
    }
}