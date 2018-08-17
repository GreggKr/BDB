package me.greggkr.bdb

import com.natpryce.konfig.ConfigurationProperties
import me.greggkr.bdb.util.Config
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import java.io.File

val config = ConfigurationProperties.fromFile(File("config.properties"))

fun main(args: Array<String>) {
    JDABuilder(AccountType.BOT)
            .setToken(config[Config.Bot.token])
            .buildBlocking()
}