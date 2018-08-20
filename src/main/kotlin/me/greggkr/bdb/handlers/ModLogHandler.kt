package me.greggkr.bdb.handlers

import me.greggkr.bdb.util.Logger
import me.greggkr.bdb.util.prettyString
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class ModLogHandler : ListenerAdapter() {
    private val cached = ArrayList<Message>()

    override fun onGuildMessageReceived(e: GuildMessageReceivedEvent) {
        cached.add(e.message)
    }

    override fun onGuildMessageDelete(e: GuildMessageDeleteEvent) {
        val msg = getCachedMessage(e.messageIdLong) ?: return
        Logger.logMessage(e.guild, "Message Deleted", msg.author.effectiveAvatarUrl, "Message by ${msg.author.prettyString()}", "Deleted Message:\n${msg.contentRaw}")

        cached.remove(msg)
    }

    override fun onGuildMessageUpdate(e: GuildMessageUpdateEvent) {
        val msg = getCachedMessage(e.messageIdLong) ?: return
        Logger.logMessage(e.guild, "Message Edited", msg.author.effectiveAvatarUrl, "Message by ${msg.author.prettyString()}", "Original Message:\n${msg.contentRaw}\n\nUpdated Message:\n${e.message.contentRaw}")

        cached.remove(msg)
        cached.add(e.message)
    }

    private fun getCachedMessage(id: Long) = cached.find { it.idLong == id }
}