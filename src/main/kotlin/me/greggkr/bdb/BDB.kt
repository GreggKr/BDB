package me.greggkr.bdb

import com.natpryce.konfig.EnvironmentVariables
import com.oopsjpeg.osu4j.backend.Osu
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import lavalink.client.io.jda.JdaLavalink
import me.diax.comportment.jdacommand.CommandHandler
import me.greggkr.bdb.handlers.ModLogHandler
import me.greggkr.bdb.util.CommandRegistry
import me.greggkr.bdb.util.Config
import me.greggkr.bdb.util.ScheduledMessager
import me.greggkr.bdb.db.Data
import me.greggkr.bdb.db.Database
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import java.net.URI

typealias JDACCommandHandler = CommandHandler

val config = EnvironmentVariables()
val handler = JDACCommandHandler()
val data = Data(Database(
        config[Config.Mongo.user],
        config[Config.Mongo.password],
        config[Config.Mongo.authDBName],
        config[Config.Mongo.dbName],
        config[Config.Mongo.host],
        config[Config.Mongo.port]
))

val osu = Osu.getAPI(config[Config.Osu.apiKey])!!

val jda = JDABuilder(AccountType.BOT)
        .setToken(config[Config.Bot.token])
        .addEventListener(me.greggkr.bdb.handlers.CommandHandler(handler), ModLogHandler())
        .build()!!

val playerManager = DefaultAudioPlayerManager()
val lavaLink = JdaLavalink(config[Config.Bot.userId], 1) { _ -> jda }

fun main(args: Array<String>) {
    setIdeaIoUseFallback()
    if (args.isNotEmpty()) {
        if (args.contains("audio")) {
            AudioSourceManagers.registerRemoteSources(playerManager)
            lavaLink.addNode(URI(config[Config.Lavalink.ws]), config[Config.Lavalink.pwd])
            jda.addEventListener(lavaLink)

            playerManager.createPlayer()
        }
    }

    handler.registerCommands(CommandRegistry.commands)
    ScheduledMessager.start()
}