package me.greggkr.bdb.util

import me.diax.comportment.jdacommand.Command
import me.greggkr.bdb.commands.admin.SettingsCommand
import me.greggkr.bdb.commands.info.AboutCommand
import me.greggkr.bdb.commands.info.TwitchCommand
import me.greggkr.bdb.commands.mod.BanCommand
import me.greggkr.bdb.commands.mod.KickCommand
import me.greggkr.bdb.commands.osu.CompositionCommand
import me.greggkr.bdb.commands.osu.SpreadCommand
import me.greggkr.bdb.commands.osu.TopPlaysCommand
import me.greggkr.bdb.commands.osu.UserCommand
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
                AboutCommand(),
                TwitchCommand(),


                /* REDO LOL */
                UserCommand(),
                TopPlaysCommand(),
                SpreadCommand(),
                CompositionCommand()
        )
    }

    private fun register(vararg commands: Command) {
        this.commands.addAll(commands)
    }
}