package me.greggkr.bdb.util

import me.diax.comportment.jdacommand.Command
import me.greggkr.bdb.commands.admin.SettingsCommand
import me.greggkr.bdb.commands.info.AboutCommand

object CommandRegistry {
    val commands = HashSet<Command>()

    init {
        register(
                /* ADMIN */
                SettingsCommand(),
                
                /* INFO */
                AboutCommand()
        )
    }

    private fun register(vararg commands: Command) {
        this.commands.addAll(commands)
    }
}