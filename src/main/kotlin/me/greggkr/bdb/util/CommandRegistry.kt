package me.greggkr.bdb.util

import me.diax.comportment.jdacommand.Command
import me.greggkr.bdb.commands.admin.SettingsCommand
import me.greggkr.bdb.commands.info.*
import me.greggkr.bdb.commands.osu.*
import me.greggkr.bdb.commands.owner.BlacklistUserCommand
import me.greggkr.bdb.commands.owner.HerokuCommand
import me.greggkr.bdb.commands.owner.LeaveServerCommand
import me.greggkr.bdb.nra.RestrictCommand

object CommandRegistry {
    val commands = HashSet<Command>()

    init {
        register(
                /* ADMIN */
                SettingsCommand(),

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
                LeaveServerCommand(),

                /* INFO */
                AboutCommand(),
                AnalysisCommand(),
                DabbyCommand(),
                HelpCommand(),
                OldestMemberCommand()
        )
    }

    private fun register(vararg commands: Command) {
        this.commands.addAll(commands)
    }
}
