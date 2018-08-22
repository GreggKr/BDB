package me.greggkr.bdb.util.logging

enum class LogType {
    /* Message */
    GUILD_MESSAGE_EDIT,
    GUILD_MESSAGE_DELETE,

    /* GUILD */
    GUILD_UPDATE,
    /* Member */
    GUILD_MEMBER_JOIN,
    GUILD_MEMBER_LEVEL,
    GUILD_MEMBER_NICK_CHANGE,
    GUILD_MEMBER_ROLE_ADD,
    GUILD_MEMBER_ROLE_REMOVE,
    /* END GUILD */
}