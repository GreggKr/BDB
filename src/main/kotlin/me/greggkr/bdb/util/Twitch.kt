package me.greggkr.bdb.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import me.greggkr.bdb.config
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

private val CLIENT_ID = config[Config.Twitch.clientId]
private val CLIENT = OkHttpClient.Builder()
        .authenticator { _, response ->
            response.request().newBuilder().addHeader("Client-ID", CLIENT_ID).build()
        }.build()

private val baseUrl = HttpUrl.parse("https://api.twitch.tv/helix")!!
private val gson = GsonBuilder()
        .setLenient()
        .setPrettyPrinting()
        .create()

object Twitch {
    private fun getStreamerId(user: String): Int? {
        val ret = makeRequest(Request.Builder()
                .url(baseUrl.newBuilder()
                        .addPathSegment("users")
                        .addQueryParameter("login", user)
                        .build())
                .get()
                .build()) ?: return null

        val data = if (ret.has("data") && ret["data"].isJsonArray) ret["data"].asJsonArray else return null
        if (data.size() == 0) return null
        return data[0].asJsonObject["id"].asInt
    }

    fun isStreaming(user: String): Boolean? {
        val id = getStreamerId(user) ?: return null
        val ret = makeRequest(Request.Builder()
                .url(baseUrl.newBuilder()
                        .addPathSegment("streams")
                        .addQueryParameter("user_id", id.toString())
                        .build())
                .get()
                .build()) ?: return null

        val data = if (ret.has("data") && ret["data"].isJsonArray) ret["data"].asJsonArray else return null
        if (data.size() == 0) return null
        return data[0].asJsonObject["type"].asString == "live"
    }

    private inline fun <reified T> makeRequest(req: Request): T? {
        val res = CLIENT.newCall(req).execute()
        val body = res.body() ?: return null

        return gson.fromJson(body.string(), T::class.java)
    }

    private fun makeRequest(req: Request): JsonObject? {
        val res = CLIENT.newCall(req).execute()
        val body = res.body() ?: return null

        return gson.fromJson(body.string(), JsonObject::class.java)
    }
}
