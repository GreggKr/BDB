package me.greggkr.bdb.logging

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

        if (title.isNotEmpty()) embed.setTitle(title)
        if (thumbnail.isNotEmpty()) embed.setThumbnail(thumbnail)
        if (footer.isNotEmpty()) embed.setFooter(footer, null)
        if (description.isNotEmpty()) embed.setDescription(description)

        channel.sendMessage(embed.build()).queue()
    }
}