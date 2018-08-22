package me.greggkr.bdb.util.logging

import me.greggkr.bdb.data
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Guild
import java.time.OffsetDateTime

object Logger {
    fun logMessage(guild: Guild, title: String = "", thumbnail: String = "", footer: String = "", description: String = "") {
        val channel = data.getModLogChannel(guild) ?: return
        val embed = EmbedBuilder()
                .setColor(data.getColor(guild))
                .setTimestamp(OffsetDateTime.now())

        if (!title.isEmpty()) embed.setTitle(title)
        if (!thumbnail.isEmpty()) embed.setThumbnail(thumbnail)
        if (!footer.isEmpty()) embed.setFooter(footer, null)
        if (!description.isEmpty()) embed.setDescription(description)

        channel.sendMessage(embed.build()).queue()
    }
}