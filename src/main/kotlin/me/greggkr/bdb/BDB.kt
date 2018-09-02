package me.greggkr.bdb

import com.natpryce.konfig.ConfigurationProperties
import me.diax.comportment.jdacommand.CommandHandler
import me.greggkr.bdb.handlers.ModLogHandler
import me.greggkr.bdb.util.CommandRegistry
import me.greggkr.bdb.util.Config
import me.greggkr.bdb.util.ScheduledMessager
import me.greggkr.bdb.util.db.Data
import me.greggkr.bdb.util.db.Database
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

typealias JDACCommandHandler = CommandHandler

val config = ConfigurationProperties.fromFile(File("config.properties"))
val handler = JDACCommandHandler()
val data = Data(Database(
        config[Config.Mongo.user],
        config[Config.Mongo.password],
        config[Config.Mongo.authDBName],
        config[Config.Mongo.dbName],
        config[Config.Mongo.host],
        config[Config.Mongo.port]
))

val jda = JDABuilder(AccountType.BOT)
        .setToken(config[Config.Bot.token])
        .addEventListener(me.greggkr.bdb.handlers.CommandHandler(handler), ModLogHandler())
        .build()!!

fun main(args: Array<String>) {
    setIdeaIoUseFallback()

    handler.registerCommands(CommandRegistry.commands)

    ScheduledMessager.start()
}