package me.greggkr.bdb.nra

import me.greggkr.bdb.data
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class RestrictEventListener : ListenerAdapter() {
    override fun onGuildMemberJoin(e: GuildMemberJoinEvent) {
        if (data.isNRARestricted(e.user.idLong)) {
            e.guild.controller.addRolesToMember(e.member, e.guild.getRoleById(RESTRICTED_ROLE_ID)).queue()
        }
    }
}