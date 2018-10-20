package me.greggkr.bdb

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.overriding
import com.oopsjpeg.osu4j.backend.Osu
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import lavalink.client.io.jda.JdaLavalink
import me.diax.comportment.jdacommand.CommandHandler
import me.greggkr.bdb.handlers.ModLogHandler
import me.greggkr.bdb.util.CommandRegistry
import me.greggkr.bdb.util.Config
import me.greggkr.bdb.util.ScheduledMessager
import me.greggkr.bdb.util.db.Data
import me.greggkr.bdb.util.db.Database
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import java.io.File
import java.net.URI

typealias JDACCommandHandler = CommandHandler

val config = ConfigurationProperties.systemProperties() overriding
        ConfigurationProperties.fromFile(File("config.properties"))
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

lateinit var jda: JDA

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