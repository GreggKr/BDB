package me.greggkr.bdb.analysis

import com.oopsjpeg.osu4j.GameMod
import lt.ekgame.beatmap_analyzer.Gamemode
import lt.ekgame.beatmap_analyzer.beatmap.osu.OsuBeatmap
import lt.ekgame.beatmap_analyzer.difficulty.Difficulty
import lt.ekgame.beatmap_analyzer.parser.BeatmapParser
import lt.ekgame.beatmap_analyzer.utils.Mod
import lt.ekgame.beatmap_analyzer.utils.Mods
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL

private const val DOWNLOAD_URL = "https://osu.ppy.sh/osu"

data class MapData(val od: Double,
                   val ar: Double,
                   val circleCount: Int,
                   val maxCombo: Int,
                   val maxHits: Int,
                   val aimDifficulty: Double,
                   val speedDifficulty: Double
)

data class Analytics(val accuracy: Double,
                     val performance: Double,
                     val accuracyPerformance: Double,
                     val speedPerformance: Double,
                     val aimPerformance: Double,
                     val mapCompletion: Double
)

data class Score(val id: Int,
                 val maxCombo: Int,
                 val n300: Int,
                 val n100: Int,
                 val n50: Int,
                 val nMiss: Int,
                 val mods: Array<GameMod>
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

fun getAcc(n300: Int,
           n100: Int,
           n50: Int,
           nMiss: Int): Double {
    val hitCount = n300 + n100 + n50 + nMiss
    if (hitCount == 0) return hitCount.toDouble()
    return (6.0 * n300 + 2.0 * n100 + n50) / (6.0 * hitCount)
}

fun getStrain(strain: Double): Double {
    return Math.pow(5.0 * Math.max(1.0, strain / 0.0675) - 4.0, 3.0) / 100000.0
}

fun parseMap(file: File, mods: Array<GameMod>): MapData {
    val baMap = BeatmapParser().parse(file)
    var bitwiseMods = 0L
    for (mod in mods) {
        bitwiseMods = bitwiseMods or mod.bit
    }
    val baMods = Mods.parse(bitwiseMods.toInt())

    var aimDifficulty = 0.0
    var speedDifficulty = 0.0
    var od = 0.0
    var ar = 0.0
    when (baMap) {
        is OsuBeatmap -> {
            val d = baMap.getDifficulty(baMods)
            aimDifficulty = d.aim
            speedDifficulty = d.speed
            od = d.od
            ar = d.ar
        }
        else -> {
            // TODO: Other gamemodes
        }
    }

    var reading = false
    var circleCount = 0
    var maxHits = 0

    file.readLines().forEach{
        if (it.startsWith("[HitObjects]")) {
            reading = true
        } else if (reading) {
            val tokens = it.split(",");
            val type = tokens[3].toInt()
            if (type and 1 == 1) { // circle
                circleCount++
            }
            maxHits++;
        }
    }
    return MapData(od, ar, circleCount, baMap.maxCombo, maxHits, aimDifficulty, speedDifficulty)
}

fun getPP(score: Score, map: MapData): Analytics {
    val od = map.od
    val ar = map.ar
    val circles = map.circleCount
    val mods = score.mods

    val acc = getAcc(score.n300, score.n100, score.n50, score.nMiss)

    var aimDifficulty = map.aimDifficulty
    // NOTE: Not supported smh
    /*if (mods.has(Mod.TOUCH_DEVICE)) {
        aimDifficulty = Math.pow(aimDifficulty, 0.8)
    }*/

    var aimValue = getStrain(aimDifficulty)

    val totalHits = score.n300 + score.n100 + score.n50 + score.nMiss
    val totalHitsOver = totalHits / 2000.0

    var lengthBonus = 0.95 + 0.4 * Math.min(1.0, totalHitsOver)
    if (totalHits > 2000.0) {
        lengthBonus += (Math.log10(totalHitsOver) * 0.5)
    }

    val missPenalty = Math.pow(0.97, score.nMiss.toDouble())

    val comboBreak = Math.pow(score.maxCombo.toDouble(), 0.8) / Math.pow(map.maxCombo.toDouble(), 0.8)

    aimValue *= lengthBonus
    aimValue *= missPenalty
    aimValue *= comboBreak

    var arBonus = 1.0

    if (ar > 10.33) {
        arBonus += 0.45 * (ar - 10.33)
    } else if (ar < 8) {
        var lowArBonus = 0.01 * (8 - ar)

        if (mods.contains(GameMod.HIDDEN)) {
            lowArBonus += lowArBonus
        }

        arBonus += lowArBonus
    }

    aimValue *= arBonus

    if (mods.contains(GameMod.HIDDEN)) {
        aimValue *= 1.02 + (11 - ar) / 50.0
    }

    if (mods.contains(GameMod.FLASHLIGHT)) {
        aimValue *= 1.45 * lengthBonus
    }

    val accBonus = 0.5 + acc / 2.0

    val odBonus = 0.98 + od * od / 2500.0

    aimValue *= accBonus
    aimValue *= odBonus

    val aimPP = aimValue

    var speedValue = getStrain(map.speedDifficulty)

    speedValue *= lengthBonus
    speedValue *= missPenalty
    speedValue *= comboBreak
    speedValue *= accBonus
    speedValue *= odBonus

    if (mods.contains(GameMod.HIDDEN)) {
        speedValue *= 1.18
    }

    val speedPP = speedValue

    var realAcc = 0.0

    if (circles > 0) {
        realAcc = ((score.n300 - (totalHits - circles)) * 300.0 + score.n100 * 100.0 + score.n50 * 50.0) / (circles * 300.0)
    }

    if (realAcc < 0.0) {
        realAcc = 0.0
    }

    var accValue = Math.pow(1.52163, od) * Math.pow(realAcc, 24.0) * 2.83

    accValue *= Math.min(1.15, Math.pow(circles / 1000.0, 0.3))

    if (mods.contains(GameMod.HIDDEN)) {
        accValue *= 1.02
    }

    if (mods.contains(GameMod.FLASHLIGHT)) {
        accValue *= 1.02
    }

    val accPP = accValue

    var finalMultiplier = 1.12

    if (mods.contains(GameMod.NO_FAIL)) {
        finalMultiplier *= 0.9
    }

    if (mods.contains(GameMod.SPUNOUT)) {
        finalMultiplier *= 0.95
    }

    val totalPP = Math.pow(Math.pow(aimValue, 1.1) + Math.pow(speedValue, 1.1) + Math.pow(accValue, 1.1), 1.0 / 1.1) * finalMultiplier

    val mapCompletion = totalHits.toDouble() / map.maxHits.toDouble()

    return Analytics(acc, totalPP, aimPP, speedPP, accPP, mapCompletion)
}

fun analyse(id: Int,
            maxCombo: Int,
            n300: Int,
            n100: Int,
            n50: Int,
            nMiss: Int,
            mods: Array<GameMod>): Analytics {
    val file = downloadMap(id)

    val score = Score(id, maxCombo, n300, n100, n50, nMiss, mods)
    val map = parseMap(file, mods)

    return getPP(score, map)
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
