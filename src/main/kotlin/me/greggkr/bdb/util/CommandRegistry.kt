package me.greggkr.bdb.util

import me.diax.comportment.jdacommand.Command
import me.greggkr.bdb.commands.admin.SettingsCommand
import me.greggkr.bdb.commands.info.AboutCommand
import me.greggkr.bdb.commands.mod.KickCommand

object CommandRegistry {
    val commands = HashSet<Command>()

    init {
        register(
                /* ADMIN */
                SettingsCommand(),

                /* MOD */
                KickCommand(),

                /* INFO */
                AboutCommand()
        )
    }

    private fun register(vararg commands: Command) {
        this.commands.addAll(commands)
    }
}