package me.greggkr.bdb.util

import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.User
import java.awt.Color
import java.text.DecimalFormat

fun Color.toHex(): String {
    return "#${Integer.toHexString(rgb).substring(2)}"
}

fun User.prettyString(): String {
    return "${this.name}#${this.discriminator}"
}

fun Double.toPercent(multiply: Boolean = true): String {
    return DecimalFormat("#.##").format(this * if (multiply) 100 else 1) + "%"
}

fun EmbedBuilder.addInlineField(title: String, description: String): EmbedBuilder {
    this.addField(title, description, true)
    return this
}

fun EmbedBuilder.addInlineField(title: String, description: Int): EmbedBuilder {
    this.addInlineField(title, description.toString())
    return this
}

fun EmbedBuilder.addInlineField(title: String, description: Double): EmbedBuilder {
    this.addInlineField(title, description.toString())
    return this
}