package me.greggkr.bdb.util

import com.natpryce.konfig.PropertyGroup
import com.natpryce.konfig.getValue
import com.natpryce.konfig.intType
import com.natpryce.konfig.stringType

class Config {
    object Bot : PropertyGroup() {
        val token by stringType
    }

    object Mongo : PropertyGroup() {
        val user by stringType
        val password by stringType
        val authDBName by stringType
        val dbName by stringType
        val host by stringType
        val port by intType
    }
}