package me.greggkr.bdb.commands.owner

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandAttribute
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.util.Emoji
import net.dv8tion.jda.core.entities.Message

@CommandDescription(name = "blacklist", triggers = [
    "blacklist"
], attributes = [
    CommandAttribute(key = "botOwnerOnly")
], description = " Allows you to blacklist a user from using the bot.")
class BlacklistUserCommand : Command {
    override fun execute(message: Message, args: String) {
        if (args.isBlank()) return

        val pms = message.author.openPrivateChannel().complete()
        if (data.isBlacklisted(args)) {
            data.unBlacklistUser(args)
            pms.sendMessage("${Emoji.WHITE_CHECK_MARK} Unblacklisted $args").queue()
        } else {
            data.blacklistUser(args)
            pms.sendMessage("${Emoji.WHITE_CHECK_MARK} Blacklisted $args").queue()
        }
    }
}