package me.greggkr.bdb.commands.owner

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandAttribute
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.util.Emoji
import me.greggkr.bdb.util.eval
import me.greggkr.bdb.util.evalImports
import me.greggkr.bdb.util.scriptingEngine
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import java.util.concurrent.TimeUnit
import javax.script.ScriptException

@CommandDescription(name = "eval", triggers = [
    "eval"
], attributes = [
    CommandAttribute(key = "botOwnerOnly")
], description = "Evaluates a string.")
class EvalCommand : Command {
    init {
        evalImports(listOf(
                "net.dv8tion.jda.core.entities.*",
                "net.dv8tion.jda.core.*",
                "java.util.*",
                "java.lang.*",
                "me.greggkr.bdb.*",
                "me.greggkr.bdb.util.*"
        ))

        TimeUnit.SECONDS.sleep(1)
    }

    override fun execute(message: Message, args: String) {
        val guild = message.guild
        val channel = message.channel

        if (args.isBlank()) return

        if (args.split(Regex("\\s+"))[0].equals("info", true)) {
            val factory = scriptingEngine.factory
            channel.sendMessage(EmbedBuilder()
                    .setColor(data.getColor(guild))
                    .addField("Language", "${factory.languageName} v${factory.languageVersion}", true)
                    .addField("Engine", "${factory.engineName} v${factory.engineVersion}", true)
                    .addField("Mimetypes", factory.mimeTypes.toString(), true)
                    .addField("Extensions", factory.extensions.toString(), true)
                    .build())
                    .queue()
            return
        }

        try {
            val map = mapOf(
                    Pair("message", message),
                    Pair("author", message.author),
                    Pair("data", data),
                    Pair("member", message.member),
                    Pair("guild", message.guild)
            )
            val res = eval(args, map)

            message.addReaction(Emoji.WHITE_CHECK_MARK.unicode).queue()
            if (res != null) {
                channel.sendMessage("$res").queue()
            }
        } catch (e: ScriptException) {
            message.addReaction(Emoji.X.unicode).queue()
            channel.sendMessage("$e").queue()
        }
    }
}