package me.greggkr.bdb.commands.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.lavaLink
import me.greggkr.bdb.playerManager
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "testaudio", triggers = [
    "testaudio"
], description = "Tests audio")
class TestAudioCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        val link = lavaLink.getLink(guild)
        link.connect(message.member.voiceState.channel)
        channel.sendMessage("connected to vc").queue()

////        AudioHandler.loadAudioTrack(channel, link.player, args)
//        val player = link.player
//        playerManager.loadItem(args, object : AudioLoadResultHandler {
//            override fun trackLoaded(track: AudioTrack) {
//                channel.sendMessage("loaded track").queue()
//                player.volume = 100
//                player.playTrack(track)
//            }
//
//            override fun playlistLoaded(playlist: AudioPlaylist) {
//                channel.sendMessage("loaded playlist").queue()
//                player.playTrack(playlist.tracks[0])
//            }
//
//            override fun loadFailed(e: FriendlyException) {
//                channel.sendMessage("failed to load: $e").queue()
//            }
//
//            override fun noMatches() {
//                channel.sendMessage("no matches").queue()
//            }
//        })
    }
}