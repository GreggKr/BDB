package me.greggkr.bdb.handlers

import me.greggkr.bdb.data
import me.greggkr.bdb.util.logging.LogType
import me.greggkr.bdb.util.logging.Logger
import me.greggkr.bdb.util.prettyString
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.guild.member.*
import net.dv8tion.jda.core.events.guild.update.*
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class ModLogHandler : ListenerAdapter() {
    private val cached = ArrayList<Message>()

    override fun onGuildMessageReceived(e: GuildMessageReceivedEvent) {
        cached.add(e.message)
    }

    override fun onGuildMessageDelete(e: GuildMessageDeleteEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_MESSAGE_DELETE)) return
        val msg = getCachedMessage(e.messageIdLong) ?: return
        Logger.logMessage(e.guild, "Message Deleted", msg.author.effectiveAvatarUrl, "Message by `${msg.author.prettyString()}`", "Deleted Message:\n${msg.contentRaw}")

        cached.remove(msg)
    }

    override fun onGuildMessageUpdate(e: GuildMessageUpdateEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_MESSAGE_EDIT)) return
        val msg = getCachedMessage(e.messageIdLong) ?: return
        Logger.logMessage(e.guild, "Message Edited", msg.author.effectiveAvatarUrl, "Message by `${msg.author.prettyString()}`", "Original Message:\n${msg.contentRaw}\n\nUpdated Message:\n${e.message.contentRaw}")

        cached.remove(msg)
        cached.add(e.message)
    }

    override fun onGuildMemberJoin(e: GuildMemberJoinEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_MEMBER_JOIN)) return

        val user = e.user
        Logger.logMessage(e.guild, "Member Joined", user.effectiveAvatarUrl, description = "`${user.prettyString()}` joined.")
    }

    override fun onGuildMemberLeave(e: GuildMemberLeaveEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_MEMBER_LEVEL)) return

        val user = e.user
        Logger.logMessage(e.guild, "Member Left", user.effectiveAvatarUrl, description = "`${user.prettyString()}` left.")
    }

    override fun onGuildMemberNickChange(e: GuildMemberNickChangeEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_MEMBER_NICK_CHANGE)) return

        val user = e.user
        Logger.logMessage(e.guild, "Nickname Change", user.effectiveAvatarUrl, description = "User `${user.prettyString()}` changed their nickname.\nPrevious:`${e.prevNick
                ?: "No Nick"}`\nNew: `${e.newNick ?: "No Nick"}`.")
    }

    override fun onGuildMemberRoleAdd(e: GuildMemberRoleAddEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_MEMBER_ROLE_ADD)) return

        val user = e.user
        Logger.logMessage(e.guild, "Role(s) Added", user.effectiveAvatarUrl, description = "Role(s) added to `${user.prettyString()}`.\n" +
                "Role(s) added: `${e.roles.map { it.name }}`\n" +
                "All roles: `${e.member.roles.map { it.name }}`")
    }

    override fun onGuildMemberRoleRemove(e: GuildMemberRoleRemoveEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_MEMBER_ROLE_REMOVE)) return

        val user = e.user
        Logger.logMessage(e.guild, "Role(s) Removed", user.effectiveAvatarUrl, description = "Role(s) removed from `${user.prettyString()}`.\n" +
                "Role(s) removed: `${e.roles.map { it.name }}`\n" +
                "All roles: `${e.member.roles.map { it.name }}`")
    }

    override fun onGuildUpdateAfkChannel(e: GuildUpdateAfkChannelEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_UPDATE)) return

        Logger.logMessage(e.guild, "Updated AFK Channel", description = "Changed AFK Channel.\nOld: `${e.oldAfkChannel?.name
                ?: "None"}`\nNew: `${e.newAfkChannel?.name ?: "None"}`.")
    }

    override fun onGuildUpdateAfkTimeout(e: GuildUpdateAfkTimeoutEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_UPDATE)) return

        Logger.logMessage(e.guild, "Updated AFK Timeout", description = "Changed AFK Timeout.\nOld: `${e.oldAfkTimeout.seconds}`\nNew: `${e.newAfkTimeout.seconds}`.")
    }

    override fun onGuildUpdateExplicitContentLevel(e: GuildUpdateExplicitContentLevelEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_UPDATE)) return

        Logger.logMessage(e.guild, "Updated Explicit Content Level", description = "Changed Explicit Content Level.\nOld: `${e.oldLevel.name}`\nNew: `${e.newLevel}`.")
    }

    override fun onGuildUpdateIcon(e: GuildUpdateIconEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_UPDATE)) return

        Logger.logMessage(e.guild, "Updated Icon Event", e.newIconUrl, description = "Changed Icon.\nOld: `${e.oldIconUrl
                ?: "None"}`\nNew: `${e.newIconUrl ?: "None"}`.")
    }

    override fun onGuildUpdateMFALevel(e: GuildUpdateMFALevelEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_UPDATE)) return

        Logger.logMessage(e.guild, "Updated MFA Level", description = "Changed MFA Level.\nOld: `${e.oldMFALevel.name}`\nNew: `${e.newMFALevel.name}`.")
    }

    override fun onGuildUpdateName(e: GuildUpdateNameEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_UPDATE)) return

        Logger.logMessage(e.guild, "Updated Guild Name", description = "Changed Guild Name.\nOld: `${e.oldName}`\nNew: `${e.newName}`.")
    }

    override fun onGuildUpdateNotificationLevel(e: GuildUpdateNotificationLevelEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_UPDATE)) return

        Logger.logMessage(e.guild, "Updated Notification Level", description = "Changed Notification Level.\nOld: `${e.oldNotificationLevel.name}`\nNew: `${e.newNotificationLevel.name}`.")
    }

    override fun onGuildUpdateOwner(e: GuildUpdateOwnerEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_UPDATE)) return

        Logger.logMessage(e.guild, "Updated Guild Owner", description = "Changed Guild Owner.\nOld: ${e.oldOwner.user.asMention}\nNew: ${e.newOwner.user.asMention}.")
    }

    override fun onGuildUpdateRegion(e: GuildUpdateRegionEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_UPDATE)) return

        Logger.logMessage(e.guild, "Updated Region", description = "Changed Region.\nOld: `${e.oldRegion.name}`\nNew: `${e.newRegion.name}`.")
    }

    override fun onGuildUpdateSplash(e: GuildUpdateSplashEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_UPDATE)) return

        Logger.logMessage(e.guild, "Updated Splash", description = "Changed Splash.\nOld: `${e.oldSplashUrl
                ?: "None"}`\nNew: `${e.newSplashUrl ?: "None"}`.")
    }

    override fun onGuildUpdateSystemChannel(e: GuildUpdateSystemChannelEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_UPDATE)) return

        Logger.logMessage(e.guild, "Updated System Channel", description = "Changed System Channel.\nOld: ${e.oldSystemChannel?.asMention
                ?: "None"}\nNew: ${e.newSystemChannel?.asMention ?: "None"}.")
    }

    override fun onGuildUpdateVerificationLevel(e: GuildUpdateVerificationLevelEvent) {
        if (!data.getLogTypeEnabled(e.guild, LogType.GUILD_UPDATE)) return

        Logger.logMessage(e.guild, "Updated AFK Channel", description = "Changed AFK Channel.\nOld: `${e.oldVerificationLevel.name}`\nNew: `${e.newVerificationLevel.name}`.")
    }

    private fun getCachedMessage(id: Long) = cached.find { it.idLong == id }
}