package me.greggkr.bdb.analysis

import com.github.francesco149.koohii.Koohii
import lt.ekgame.beatmap_analyzer.difficulty.Difficulty
import lt.ekgame.beatmap_analyzer.parser.BeatmapParser
import lt.ekgame.beatmap_analyzer.performance.Performance
import lt.ekgame.beatmap_analyzer.utils.Mods
import lt.ekgame.beatmap_analyzer.utils.ScoreVersion
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL

typealias AnalyzerScore = lt.ekgame.beatmap_analyzer.performance.scores.Score

private const val DOWNLOAD_URL = "https://osu.ppy.sh/osu"

class Analysis(val map: Koohii.Map)

data class Score(
        val score: Int,
        val id: Int,
        val maxCombo: Int,
        val n300: Int,
        val n100: Int,
        val n50: Int,
        val nMiss: Int
)

val bmCacheFolder = File("bm_cache/")
fun analyse(score: Score, version: ScoreVersion): Performance {
    val map = BeatmapParser().parse(downloadMap(score.id))
    return map.difficulty.getPerformance(AnalyzerScore.of(map)
            .score(score.score)
            .osuAccuracy(score.n100, score.n50, score.nMiss)
            .combo(score.maxCombo)
            .version(version)
            .build())
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