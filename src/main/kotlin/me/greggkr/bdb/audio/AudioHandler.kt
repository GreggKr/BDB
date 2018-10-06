package me.greggkr.bdb.audio

import lavalink.client.io.jda.JdaLavalink
import lavalink.client.player.LavalinkPlayer
import net.dv8tion.jda.core.entities.Guild

fun JdaLavalink.getPlayer(guild: Guild): LavalinkPlayer {
    val player = this.getLink(guild).player
    player.addListener(TrackScheduler(guild))
    return player
}