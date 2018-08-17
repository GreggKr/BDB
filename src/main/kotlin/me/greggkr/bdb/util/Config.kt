package me.greggkr.bdb.util

import com.natpryce.konfig.PropertyGroup
import com.natpryce.konfig.getValue
import com.natpryce.konfig.stringType

class Config {
    object Bot : PropertyGroup() {
        val token by stringType
    }
}