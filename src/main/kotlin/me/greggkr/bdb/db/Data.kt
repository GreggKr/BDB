package me.greggkr.bdb.db

import me.greggkr.bdb.logging.LogType
import me.greggkr.bdb.util.toHex
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Role
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.entities.User
import java.awt.Color

class Data(private val db: Database) {
    private val defaultPrefix = "!"
    private val defaultColor = Color.decode("#934cff")

    private val prefixes = HashMap<String, String>()

    private val owners = listOf(
            184041169796333568, // Gregg
            184733130123378688, // Landon
            216596910792769537, // Yosh
            131392299514068992, // Hand
            199374658200403969 // Michael (Plowayy)
    )

    fun isOwner(user: User) = owners.contains(user.idLong)

    fun getPrefix(guild: Guild): String {
        if (prefixes.contains(guild.id)) return prefixes[guild.id]!!

        val prefix = db.getPrefix(guild.id) ?: defaultPrefix

        prefixes[guild.id] = prefix
        return prefix
    }

    fun setPrefix(guild: Guild, prefix: String) {
        db.setPrefix(guild.id, prefix)
        prefixes[guild.id] = prefix
    }

    fun getModLogChannel(guild: Guild): TextChannel? {
        val ch = db.getModLogChannel(guild.id) ?: return null
        return guild.getTextChannelById(ch)
    }

    fun setModLogChannel(guild: Guild, channel: TextChannel) {
        db.setModLogChannel(guild.id, channel.idLong)
    }

    fun getColor(guild: Guild): Color {
        val raw = db.getColor(guild.id) ?: return defaultColor

        return Color.decode(raw) ?: defaultColor
    }

    fun setColor(guild: Guild, color: Color) {
        db.setColor(guild.id, color.toHex())
    }

    fun getLogTypeEnabled(guild: Guild, type: LogType): Boolean {
        return db.getLogTypeEnabled(guild.id, type.name) ?: false
    }

    fun setLogTypeEnabled(guild: Guild, type: LogType, enabled: Boolean) {
        db.setLogTypeEnabled(guild.id, type.name, enabled)
    }

    fun getModRole(guild: Guild): Role? {
        val id = db.getModRole(guild.id) ?: return null
        return guild.getRoleById(id)
    }

    fun setModRole(guild: Guild, role: Role) {
        db.setModRole(guild.id, role.id)
    }

    fun getOsuUser(user: User): String? {
        return db.getOsuUser(user.id)
    }

    fun setOsuUser(user: User, name: String) {
        db.setOsuUser(user.id, name)
    }
}