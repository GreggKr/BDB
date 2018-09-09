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

//    private val special = mapOf(
//            Pair(216596910792769537, listOf(
//                    "go to sleep $PLACEHOLDER", "lol ur bad $PLACEHOLDER"
//            ))
//    )

    fun start() {
        scheduler.scheduleAtFixedRate({
            val channel = jda.getTextChannelById(CHANNEL_ID) ?: return@scheduleAtFixedRate
            val members = jda.getGuildById(channel.guild.id).members
            val member = members[Random().nextInt(members.size)]

//            val user = member.user
//            if (user.idLong in special) {
//                val msgs = special[user.idLong]!!
//
//                channel.sendMessage(msgs[Random().nextInt(msgs.size)].replace(PLACEHOLDER, user.asMention)).queue()
//            } else {
            channel.sendMessage(messages[Random().nextInt(messages.size)].replace(PLACEHOLDER, member.asMention)).queue()
//            val msg = jda.getTextChannelById(469977153149337603).sendMessage("${jda.getGuildById(channel.guild.id).getMemberById(315563167059279882).user.asMention} welcome mate").complete()
//            msg.delete().queue()
//            }
        }, 24, 24, TimeUnit.HOURS)
    }
}
