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
            184733130123378688 // Landon
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

    fun getOsuUser(guild: Guild, user: User): String? {
        return db.getOsuUser(guild.id, user.id)
    }

    fun setOsuUser(guild: Guild, user: User, name: String) {
        db.setOsuUser(guild.id, user.id, name)
    }

    fun blacklistUser(id: String) {
        db.addBlacklistedUser(id)
    }

    fun unBlacklistUser(id: String) {
        db.removeBlacklistedUser(id)
    }

    fun isBlacklisted(id: String): Boolean {
        return db.isBlacklisted(id)
    }

    fun getAllOsuUsers(guild: Guild): Map<User, String>? {
        val users = db.getAllOsuUsers(guild.id) ?: return null
        val map = mutableMapOf<User, String>()
        for (pair in users) {
            val user = guild.jda.getUserById(pair.key) ?: continue
            map[user] = pair.value
        }

        return map
    }

   fun isNRARestricted(id: Long): Boolean {
       return id in db.getNRARestricted()
   }

    fun nraRestrictUser(id: Long) {
        db.addNRARestricted(id)
    }

    fun nraUnrestrictUser(id: Long) {
        db.removeNRARestricted(id)
    }
}