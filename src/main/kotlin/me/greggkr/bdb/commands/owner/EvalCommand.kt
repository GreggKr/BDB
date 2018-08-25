package me.greggkr.bdb.commands.owner

import me.diax.comportment.jdacommand.Command
import me.diax.comportment.jdacommand.CommandAttribute
import me.diax.comportment.jdacommand.CommandDescription
import me.greggkr.bdb.data
import me.greggkr.bdb.util.Emoji
import me.greggkr.bdb.util.eval
import me.greggkr.bdb.util.evalImports
import net.dv8tion.jda.core.entities.Message
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
                "me.greggkr.bdb.*"
        ))
    }

    override fun execute(message: Message, args: String) {
        val channel = message.channel

        try {
            val map = mapOf(
                    Pair("message", message),
                    Pair("author", message.author),
                    Pair("data", data),
                    Pair("member", message.member),
                    Pair("guild", message.guild)
            )
            val res = eval(args, map)

            channel.sendMessage("${res ?: "Returned nothing."}").queue()
        } catch (e: ScriptException) {
            channel.sendMessage("${Emoji.X} Failed to execute.").queue()
            channel.sendMessage("$e").queue()
        }
    }
}