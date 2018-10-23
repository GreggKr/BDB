package me.greggkr.bdb.util

enum class Emoji(val unicode: String) {
    X("\u274C"),
    WHITE_CHECK_MARK("\u2705"),
    THUMBS_UP("\uD83D\uDC4D"),
    ARROW_DOUBLE_UP("\u23EB"),
    ARROW_DOUBLE_DOWN("\u23EC");

    override fun toString() = unicode
}