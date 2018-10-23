package me.greggkr.bdb.twitch

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import me.greggkr.bdb.config
import me.greggkr.bdb.util.Config
import me.greggkr.bdb.twitch.`object`.Game
import me.greggkr.bdb.twitch.`object`.User
import me.greggkr.bdb.twitch.`object`.streams.StreamUser
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

private val CLIENT_ID = config[Config.Twitch.clientId]

private val CLIENT = OkHttpClient.Builder()
        .authenticator { _, response ->
            response.request().newBuilder().addHeader("Client-ID", CLIENT_ID).build()
        }
        .build()

private val baseUrl = HttpUrl.parse("https://api.twitch.tv/helix")!!
private val gson = GsonBuilder()
        .setLenient()
        .setPrettyPrinting()
        .create()

private val cachedGames = mutableMapOf<String, Game>()

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

        val data = if (ret.has("data") && ret["data"].isJsonArray) ret["data"].asJsonArray else return false
        if (data.size() == 0) return false
        return data[0].asJsonObject["type"].asString == "live"
    }

    fun getUser(user: String): User? {
        val ret = makeRequest(Request.Builder()
                .url(baseUrl.newBuilder()
                        .addPathSegment("users")
                        .addQueryParameter("login", user)
                        .build()
                )
                .get()
                .build()) ?: return null

        val data = if (ret.has("data") && ret["data"].isJsonArray) ret["data"].asJsonArray else return null
        if (data.size() == 0) return null
        return gson.fromJson(data[0].asJsonObject, User::class.java)
    }

    fun getUserById(id: String): User? {
        val ret = makeRequest(Request.Builder()
                .url(baseUrl.newBuilder()
                        .addPathSegment("users")
                        .addQueryParameter("id", id)
                        .build()
                )
                .get()
                .build()) ?: return null

        val data = if (ret.has("data") && ret["data"].isJsonArray) ret["data"].asJsonArray else return null
        if (data.size() == 0) return null
        return gson.fromJson(data[0].asJsonObject, User::class.java)
    }

    fun getUsers(users: List<String>): Array<User>? {
        val urlBuilder = baseUrl.newBuilder()
                .addPathSegment("users")

        for (user in users) {
            urlBuilder.addQueryParameter("login", user)
        }

        val ret = makeRequest(Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build()) ?: return null

        val data = if (ret.has("data") && ret["data"].isJsonArray) ret["data"].asJsonArray else return null
        if (data.size() == 0) return null
        return gson.fromJson(data, Array<User>::class.java)
    }

    /**
     * @param streamer the username of the streamer
     * @param limit the amount of followers to retrieve
     *
     * @return pair of the amount of followers and "limit" followers' ids
     */
    fun getFollowers(streamer: String, limit: Int = 1): Pair<Int, List<String>>? {
        if (limit < 1 || limit > 100) return null

        val id = getStreamerId(streamer) ?: return null

        val ret = makeRequest(Request.Builder()
                .url(baseUrl
                        .newBuilder()
                        .addPathSegments("users/follows")
                        .addQueryParameter("to_id", id.toString())
                        .addQueryParameter("first", limit.toString())
                        .build())
                .get()
                .build()) ?: return null

        if (ret.has("total") && ret["total"].asInt == 0) return null

        val data = ret.get("data")?.asJsonArray ?: return null
        val users = data.map { it.asJsonObject["from_id"].asString }
        return Pair(ret["total"].asInt, users)
    }

    fun getGame(game: String): Game? {
        val name = game.toLowerCase()

        if (name in cachedGames) return cachedGames[name]!!

        val ret = makeRequest(Request.Builder()
                .url(baseUrl
                        .newBuilder()
                        .addPathSegments("games")
                        .addQueryParameter("name", name)
                        .build())
                .get()
                .build()) ?: return null

        val data = ret["data"]?.asJsonArray ?: return null
        if (data.size() == 0) return null
        val g = gson.fromJson(data[0].asJsonObject, Game::class.java)

        cachedGames[name] = g
        return g
    }

    fun getTopLiveStreamers(game: String, amount: Int = 1): Array<StreamUser>? {
        val g = getGame(game) ?: return null

        val ret = makeRequest(Request.Builder()
                .url(baseUrl
                        .newBuilder()
                        .addPathSegments("streams")
                        .addQueryParameter("game_id", g.id)
                        .addQueryParameter("first", amount.toString())
                        .build())
                .get()
                .build()) ?: return null

        val data = ret["data"]?.asJsonArray ?: return null
        if (data.size() == 0) return null
        return gson.fromJson(data, Array<StreamUser>::class.java)
    }

    private fun makeRequest(req: Request): JsonObject? {
        val res = CLIENT.newCall(req).execute()
        val body = res.body() ?: return null

        return gson.fromJson(body.string(), JsonObject::class.java)
    }
}
