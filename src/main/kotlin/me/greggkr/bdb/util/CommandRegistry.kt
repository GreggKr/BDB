package me.greggkr.bdb.util

import me.diax.comportment.jdacommand.Command
import me.greggkr.bdb.commands.admin.SettingsCommand
import me.greggkr.bdb.commands.info.AboutCommand
import me.greggkr.bdb.commands.info.AnalysisCommand
import me.greggkr.bdb.commands.info.DabbyCommand
import me.greggkr.bdb.commands.info.HelpCommand
import me.greggkr.bdb.commands.mod.BanCommand
import me.greggkr.bdb.commands.mod.KickCommand
import me.greggkr.bdb.commands.osu.*
import me.greggkr.bdb.commands.owner.BlacklistUserCommand
import me.greggkr.bdb.commands.owner.HerokuCommand
import me.greggkr.bdb.nra.RestrictCommand

object CommandRegistry {
    val commands = HashSet<Command>()

    init {
        register(
                /* ADMIN */
                SettingsCommand(),

                /* MOD */
                BanCommand(),
                KickCommand(),

                /* NRA */
                RestrictCommand(),

                /* OSU */
                CompositionCommand(),
                MultiCommand(),
                ProfileCommand(),
                RecentBestCommand(),
                RecentCommand(),
                SpreadCommand(),
                TopAccCommand(),
                TopPlaysCommand(),
                UserCommand(),
                UsernameCommand(),

                /* OWNER */
                BlacklistUserCommand(),
//                EvalCommand(),
                HerokuCommand(),

                /* INFO */
                AboutCommand(),
                AnalysisCommand(),
                DabbyCommand(),
                HelpCommand()
        )
    }

    private fun register(vararg commands: Command) {
        this.commands.addAll(commands)
    }
}
