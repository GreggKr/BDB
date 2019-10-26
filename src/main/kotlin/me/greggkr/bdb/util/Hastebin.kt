package me.greggkr.bdb.util

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

private const val URL = "https://pastie.io/"

object Hastebin {
    private val httpClient = OkHttpClient.Builder().build()

    fun hastebin(text: String): String? {
        return try {
            val data = httpClient.newCall(
                    Request.Builder()
                            .url("$URL/documents")
                            .post(RequestBody.create(MediaType.parse("text/plain"), text))
                            .build())
                    .execute()
                    .body()
                    ?.string()

            "$URL/${JSONObject(data)["key"]}"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}