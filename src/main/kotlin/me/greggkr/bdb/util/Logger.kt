package me.greggkr.bdb.util

import me.greggkr.bdb.data
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Guild
import java.time.OffsetDateTime

object Logger {
    fun logMessage(guild: Guild, title: String, thumbnail: String, footer: String, description: String) {
        val channel = data.getModLogChannel(guild) ?: return
        channel.sendMessage(EmbedBuilder()
                .setColor(data.getColor(guild))
                .setTitle(title)
                .setDescription(description)
                .setThumbnail(thumbnail)
                .setFooter(footer, null)
                .setTimestamp(OffsetDateTime.now())
                .build())
                .queue()
    }
}