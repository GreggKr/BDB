package me.greggkr.bdb

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import lavalink.client.io.jda.JdaLavalink
import me.diax.comportment.jdacommand.CommandHandler
import me.greggkr.bdb.db.Data
import me.greggkr.bdb.db.Database
import me.greggkr.bdb.handlers.ModLogHandler
import me.greggkr.bdb.util.CommandRegistry
import me.greggkr.bdb.util.Config
import me.greggkr.bdb.util.ScheduledMessager
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import java.io.File
import java.net.URI
import java.text.DecimalFormat

typealias JDACCommandHandler = CommandHandler

val configFile = File("config.properties")
val config = if (configFile.exists()) {
    ConfigurationProperties.fromFile(configFile)
} else {
    EnvironmentVariables()
}

val handler = JDACCommandHandler()
val data = Data(Database(
        config[Config.Mongo.user],
        config[Config.Mongo.password],
        config[Config.Mongo.authDBName],
        config[Config.Mongo.dbName],
        config[Config.Mongo.host],
        config[Config.Mongo.port]
))

val test = Osu

val osu = Osu.getAPI(config[Config.Osu.apiKey])!!

val jda = JDABuilder(AccountType.BOT)
        .setToken(config[Config.Bot.token])
        .addEventListener(me.greggkr.bdb.handlers.CommandHandler(handler), ModLogHandler())
        .build()!!

val playerManager = DefaultAudioPlayerManager()
val lavaLink = JdaLavalink(config[Config.Bot.userId], 1) { jda }

val starFormat = DecimalFormat("##.##")
val scoreFormat = DecimalFormat("###,###,###,###")

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
