package me.greggkr.bdb.util

import me.diax.comportment.jdacommand.Command
import java.time.Duration

val analysisData = mutableMapOf<Command, MutableList<AnalysisResults>>()

data class AnalysisResults(val duration: Duration)