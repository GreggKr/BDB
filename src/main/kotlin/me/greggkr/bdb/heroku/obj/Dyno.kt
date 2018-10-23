package me.greggkr.bdb.heroku.obj

import com.google.gson.annotations.SerializedName

data class Dyno(
        @SerializedName("attach_url") val attachUrl: String,
        @SerializedName("command") val command: String,
        @SerializedName("created_at") val createdAt: String,
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String,
        @SerializedName("release") val release: Release,
        @SerializedName("app") val app: App,
        @SerializedName("size") val size: String,
        @SerializedName("state") val state: String,
        @SerializedName("type") val type: String,
        @SerializedName("updated_at") val updatedAt: String
)