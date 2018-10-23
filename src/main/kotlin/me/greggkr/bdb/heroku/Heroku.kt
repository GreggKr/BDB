package me.greggkr.bdb.heroku

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.greggkr.bdb.config
import me.greggkr.bdb.heroku.obj.Dyno
import me.greggkr.bdb.util.Config
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

private val BASE_URL = HttpUrl.parse("https://api.heroku.com")!!

private val API_KEY = config[Config.Heroku.apiToken]

private val gson = GsonBuilder().create()
private val client = OkHttpClient.Builder()
        .addInterceptor {
            it.proceed(it
                    .request()
                    .newBuilder()
                    .header("Accept", "application/vnd.heroku+json; version=3")
                    .build())
        }
        .authenticator { _, response ->
            response
                    .request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .build()
        }
        .build()

class Heroku {
    companion object {
        fun listDynos(app: String): Array<Dyno>? {
            val ret = makeRequest(Request.Builder()
                    .url("https://api.heroku.com/apps/$app/dynos")
                    .get()
                    .build()) ?: return null

            return gson.fromJson(ret, Array<Dyno>::class.java)
        }

        fun restartDyno(app: String, dyno: String): Boolean? {
            val res = client.newCall(Request.Builder()
                    .url("https://api.heroku.com/apps/$app/dynos/$dyno")
                    .delete()
                    .build()).execute()

            return res.isSuccessful
        }

        private fun makeRequest(req: Request): JsonElement? {
            val res = client.newCall(req).execute()
            val body = res.body() ?: return null

            return gson.fromJson(body.string(), JsonElement::class.java)
        }
    }
}