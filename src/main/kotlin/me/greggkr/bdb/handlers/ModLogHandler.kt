package me.greggkr.bdb.handlers

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class ModLogHandler : ListenerAdapter() {
    override fun onGuildMessageDelete(e: GuildMessageDeleteEvent) {

    }

    private fun sendModLogMessage(guild: Guild, message: String) {
        
    }
}