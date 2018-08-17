package me.greggkr.bdb.handlers

import me.greggkr.bdb.data
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import java.time.OffsetDateTime

class ModLogHandler : ListenerAdapter() {
    override fun onGuildMessageDelete(e: GuildMessageDeleteEvent) {
        sendModLogMessage(e.guild, "deleted a msg")
    }

    // TODO: make pretty
    private fun sendModLogMessage(guild: Guild, message: String) {
        val channel = data.getModLogChannel(guild) ?: return

        channel.sendMessage(EmbedBuilder()
                .setDescription(message)
                .setTimestamp(OffsetDateTime.now())
                .build()).queue()
    }
}