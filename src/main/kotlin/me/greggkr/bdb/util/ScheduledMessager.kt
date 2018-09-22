package me.greggkr.bdb.util

import me.greggkr.bdb.jda
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val PLACEHOLDER = "#person"
private const val CHANNEL_ID = 434004583300202497

object ScheduledMessager {
    private val scheduler = Executors.newSingleThreadScheduledExecutor()!!

    private val messages = listOf(
            "set good scores today cutie $PLACEHOLDER ${Emoji.THUMBS_UP}",
            "owo ily $PLACEHOLDER",
            "$PLACEHOLDER ur cute lol",
            "im cold wanna cuddle, $PLACEHOLDER?"
    )

    fun start() {
        scheduler.scheduleAtFixedRate({
            val channel = jda.getTextChannelById(CHANNEL_ID) ?: return@scheduleAtFixedRate
            val members = jda.getGuildById(channel.guild.id).members
            val member = members[Random().nextInt(members.size)]

            channel.sendMessage(messages[Random().nextInt(messages.size)].replace(PLACEHOLDER, member.asMention)).queue()
        }, 24, 24, TimeUnit.HOURS)
    }
}
