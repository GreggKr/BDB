package me.greggkr.bdb.heroku.obj

import com.google.gson.annotations.SerializedName

data class Release(
        @SerializedName("id") val id: String,
        @SerializedName("version") val version: Int
)