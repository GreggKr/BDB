package me.greggkr.bdb.heroku.obj

import com.google.gson.annotations.SerializedName

data class App(
        @SerializedName("name") val name: String,
        @SerializedName("id") val id: String
)