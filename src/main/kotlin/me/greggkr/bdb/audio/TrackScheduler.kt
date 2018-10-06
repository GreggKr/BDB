package me.greggkr.bdb.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import lavalink.client.player.event.AudioEventAdapterWrapped
import me.greggkr.bdb.lavaLink
import net.dv8tion.jda.core.entities.Guild

val queues = mutableMapOf<Guild, LinkedHashSet<AudioTrack>>()

fun addToQueue(guild: Guild, track: AudioTrack) {
    val current = queues[guild] ?: LinkedHashSet()
    current.add(track)
    queues[guild] = current
}

fun addAllToQueue(guild: Guild, tracks: List<AudioTrack>) {
    val current = queues[guild] ?: LinkedHashSet()
    current.addAll(tracks)
    queues[guild] = current
}

class TrackScheduler(val guild: Guild) : AudioEventAdapterWrapped() {
    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        playNext()
    }

    override fun onTrackStuck(player: AudioPlayer, track: AudioTrack, thresholdMs: Long) {
        playNext()
    }

    override fun onTrackException(player: AudioPlayer, track: AudioTrack, exception: FriendlyException) {
        playNext()
    }

    private fun playNext() {
        if (guild !in queues) return

        val next = queues[guild]?.single()
        lavaLink.getPlayer(guild).playTrack(next)
        queues[guild]?.remove(next)
    }
}