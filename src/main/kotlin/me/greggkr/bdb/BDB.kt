package me.greggkr.bdb

import com.google.common.base.Stopwatch
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.oopsjpeg.osu4j.backend.Osu
import me.diax.comportment.jdacommand.CommandHandler
import me.greggkr.bdb.db.Data
import me.greggkr.bdb.db.Database
import me.greggkr.bdb.handlers.ModLogHandler
import me.greggkr.bdb.nra.RestrictEventListener
import me.greggkr.bdb.util.CommandRegistry
import me.greggkr.bdb.util.Config
import me.greggkr.bdb.util.ScheduledMessager
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

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

val osu = Osu.getAPI(config[Config.Osu.apiKey])!!

val jda = JDABuilder(AccountType.BOT)
        .setToken(config[Config.Bot.token])
        .addEventListener(me.greggkr.bdb.handlers.CommandHandler(handler), ModLogHandler(), RestrictEventListener())
        .build()!!

val starFormat = DecimalFormat("##.##")
val ppFormat = DecimalFormat("######.##pp")
val percentFormat = DecimalFormat("##.#%")
val accuracyFormat = DecimalFormat("###.##%")
val twoDecFormat = DecimalFormat("##.##")

var startupTime: Long = 0

fun main() {
    val timer = Stopwatch.createStarted()
    setIdeaIoUseFallback()
    handler.registerCommands(CommandRegistry.commands)
    ScheduledMessager.start()
    startupTime = timer.stop().elapsed(TimeUnit.MILLISECONDS)
    LoggerFactory.getLogger("Main").info("Startup Time: ${startupTime}ms")
}
