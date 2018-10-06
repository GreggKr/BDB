package me.greggkr.bdb.commands.audio

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.audio.queues
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "queue", triggers = [
    "queue"
], description = "Displays the current queue for the guild.")
class QueueCommand : Command {
    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        val queue = queues[guild]
        if (queue == null || queue.isEmpty()) {
            channel.sendMessage("no songs in queue").queue()
            return
        }

        val sb = StringBuilder()
        for (track in queue.iterator()) {
            sb.append(track.info.title).append('\n')
        }
        channel.sendMessage("```$sb```").queue()
    }
}