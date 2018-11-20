package me.greggkr.bdb.util

import me.diax.comportment.jdacommand.Command
import me.greggkr.bdb.commands.admin.SettingsCommand
import me.greggkr.bdb.commands.audio.*
import me.greggkr.bdb.commands.info.AboutCommand
import me.greggkr.bdb.commands.info.DabbyCommand
import me.greggkr.bdb.commands.info.HelpCommand
import me.greggkr.bdb.commands.info.TwitchCommand
import me.greggkr.bdb.commands.mod.BanCommand
import me.greggkr.bdb.commands.mod.KickCommand
import me.greggkr.bdb.commands.osu.*
import me.greggkr.bdb.commands.owner.EvalCommand
import me.greggkr.bdb.commands.owner.HerokuCommand

object CommandRegistry {
    val commands = HashSet<Command>()

    init {
        register(
                /* ADMIN */
                SettingsCommand(),

                /* AUDIO */
                ConnectCommand(),
                DisconnectCommand(),
                NowPlayingCommand(),
                PauseCommand(),
                PlayCommand(),
                QueueCommand(),
                SkipCommand(),

                /* MOD */
                BanCommand(),
                KickCommand(),

                /* OSU */
                MultiCommand(),

                /* OWNER */
                EvalCommand(),
                HerokuCommand(),

                /* INFO */
                AboutCommand(),
                HelpCommand(),
                TwitchCommand(),

                DabbyCommand(),

                /* REDO LOL */
                UserCommand(),
//                TopPlaysCommand(),
                SpreadCommand(),
                CompositionCommand(),
                AudioStatsCommand(),
//                TopAccCommand(),
                RecentCommand()
        )
    }

    private fun register(vararg commands: Command) {
        this.commands.addAll(commands)
    }
}