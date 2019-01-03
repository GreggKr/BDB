package me.greggkr.bdb.analysis

import lt.ekgame.beatmap_analyzer.difficulty.Difficulty
import lt.ekgame.beatmap_analyzer.parser.BeatmapParser
import lt.ekgame.beatmap_analyzer.performance.Performance
import lt.ekgame.beatmap_analyzer.utils.Mods
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL

typealias BAScore = lt.ekgame.beatmap_analyzer.performance.scores.Score

private const val DOWNLOAD_URL = "https://osu.ppy.sh/osu"

data class Analytics(val accuracy: Double,
                     val performance: Double,
                     val accuracyPerformance: Double,
                     val speedPerformance: Double,
                     val aimPerformance: Double
)

data class Score(val id: Int,
                 val maxCombo: Int,
                 val n300: Int,
                 val n100: Int,
                 val n50: Int,
                 val nMiss: Int,
                 val mods: Int
)

val bmCacheFolder = File("bm_cache/")
//fun analyse(score: Score): Koohii.PPv2? {
//    val reader = BufferedReader(FileReader(downloadMap(score.id)))
//    val map = Koohii.Parser().map(reader)
//    val ppv2 = Koohii.PPv2Parameters()
//
//    val stars = Koohii.DiffCalc()
//    stars.beatmap = map
//
//    ppv2.beatmap = map
//    ppv2.aim_stars = stars.aim
//    ppv2.speed_stars = stars.speed
//    ppv2.mods = score.mods
//    ppv2.n300 = 1
//    ppv2.n100 = score.n100
//    ppv2.n50 = score.n50
//    ppv2.nmiss = score.nMiss
//    ppv2.combo = score.maxCombo
//
//    return Koohii.PPv2(ppv2)
//}

fun analyse(id: Int,
            n300: Int,
            n100: Int,
            n50: Int,
            nMiss: Int,
            mods: Int): Analytics {
    val file = downloadMap(id)
    val map = BeatmapParser().parse(file)

    val objectCount = n300 + n100 + n50 + nMiss
    val fixedAcc = n300 * 300 + n100 * 100 + n50 * 50 / (objectCount * 300.0)

    val s = BAScore.of(map)
            .osuAccuracy(n100, n50, nMiss)
            .build()

    val baResult = map.getDifficulty(Mods.parse(mods)).getPerformance(s)
    return Analytics(/*baResult.accuracy*/fixedAcc, baResult.performance, baResult.accuracyPerformance, baResult.speedPerformance, baResult.aimPerformance)
}

fun calculateDifficulty(id: Int, mods: String): Difficulty {
    val map = BeatmapParser().parse(downloadMap(id))
    return map.getDifficulty(Mods.parse(mods))
}

private fun downloadMap(id: Int): File {
    bmCacheFolder.mkdir()

    val file = File(bmCacheFolder, "$$id.osu")
    if (!file.exists()) {
        file.createNewFile()
        FileUtils.copyURLToFile(URL("$DOWNLOAD_URL/$id"), file)
    }

    return file
}
