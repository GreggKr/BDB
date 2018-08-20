package me.greggkr.bdb.util

import net.dv8tion.jda.core.entities.User
import java.awt.Color

fun Color.toHex(): String {
    return "#${Integer.toHexString(rgb).substring(2)}"
}

fun User.prettyString(): String {
    return "${this.name}#${this.discriminator}"
}