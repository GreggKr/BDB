package me.greggkr.bdb.commands.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.audio.addAllToQueue
import me.greggkr.bdb.audio.addToQueue
import me.greggkr.bdb.audio.getPlayer
import me.greggkr.bdb.audio.queues
import me.greggkr.bdb.lavaLink
import me.greggkr.bdb.playerManager
import me.greggkr.bdb.util.Emoji
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "play", triggers = [
    "play"
], description = "Allows you to play music")
class PlayCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        val player = lavaLink.getPlayer(guild)

        playerManager.loadItem("ytsearch:$args", object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                if (player.playingTrack != null) {
                    addToQueue(guild, track)
                    channel.sendMessage("Added ${track.info.title} to queue.").queue()
                    return
                }

                player.playTrack(track)
                channel.sendMessage("Started playing ${track.info.title}.").queue()
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                if (playlist.isSearchResult) {
                    trackLoaded(playlist.tracks[0])
                    return
                }
                if (player.playingTrack != null) {
                    addAllToQueue(guild, playlist.tracks)
                    channel.sendMessage("Added ${playlist.tracks.size} songs to queue.").queue()
                    return
                }

                val toPlay = playlist.tracks[0]
                val toAdd = playlist.tracks.subList(1, playlist.tracks.size - 1)
                player.playTrack(toPlay)
                addAllToQueue(guild, toAdd)
                channel.sendMessage("${Emoji.WHITE_CHECK_MARK} Started playing ${toPlay.info.title}.").queue()
                channel.sendMessage("Added ${toAdd.size} songs to queue.").queue()

            }

            override fun loadFailed(exception: FriendlyException) {
                channel.sendMessage("Failed to load for $args.").queue()
            }

            override fun noMatches() {
                channel.sendMessage("No matches found for $args.").queue()
            }
        })
    }
}