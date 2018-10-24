package me.greggkr.bdb.e9

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*

private val gson = GsonBuilder().create()
private val client = OkHttpClient.Builder()
        .build()

class E926 {
    companion object {
        fun getImage(tags: String?): String? {
            val url = "https://e926.net/post/index.json${if(!tags.isNullOrEmpty()) "?tags=$tags" else ""}"

            val ret = makeRequest(Request.Builder()
                    .url(url)
                    .get()
                    .build()) ?: return null


            val list = ret.asJsonArray
            if (list.size() < 1) return null

            val rand = list[Random().nextInt(list.size())]

            return rand?.asJsonObject?.get("file_url")?.asString
        }

        private fun makeRequest(req: Request): JsonElement? {
            val res = client.newCall(req).execute()
            val body = res.body() ?: return null

            return gson.fromJson(body.string(), JsonElement::class.java)
        }
    }
}