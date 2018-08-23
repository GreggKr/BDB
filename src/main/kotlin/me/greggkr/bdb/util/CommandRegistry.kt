package me.greggkr.bdb.util

import me.diax.comportment.jdacommand.Command
import me.greggkr.bdb.commands.admin.SettingsCommand
import me.greggkr.bdb.commands.info.AboutCommand
import me.greggkr.bdb.commands.mod.BanCommand
import me.greggkr.bdb.commands.mod.KickCommand
import me.greggkr.bdb.commands.owner.EvalCommand

object CommandRegistry {
    val commands = HashSet<Command>()

    init {
        register(
                /* ADMIN */
                SettingsCommand(),

                /* MOD */
                BanCommand(),
                KickCommand(),

                /* OWNER */
                EvalCommand(),

                /* INFO */
                AboutCommand()
        )
    }

    private fun register(vararg commands: Command) {
        this.commands.addAll(commands)
    }
}