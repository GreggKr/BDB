package me.greggkr.bdb.db

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import org.bson.Document

class Database(user: String,
               password: String,
               authDBName: String,
               dbName: String,
               host: String,
               port: Int) {

    private val creds = MongoCredential.createCredential(user, authDBName, password.toCharArray())
    private val client = MongoClient(ServerAddress(host, port), creds, MongoClientOptions
            .builder()
            .addServerMonitorListener(MongoServerMonitorListener())
            .addClusterListener(MongoClusterListener())
            .addCommandListener(MongoCommandListener())
            .addConnectionPoolListener(MongoConnectionPoolListener())
            .addServerListener(MongoServerListener())
            .build())
    private val database = client.getDatabase(dbName)
    private val updateOptions = UpdateOptions().upsert(true)

    fun getPrefix(id: String): String? {
        val doc = getDoc(id, "prefixes")
        return if (doc == null) null else doc["prefix"] as String
    }

    fun setPrefix(id: String, prefix: String) {
        saveField(id, "prefixes", Document().append("prefix", prefix))
    }

    fun getModLogChannel(id: String): Long? {
        val doc = getDoc(id, "modlogchannels")
        return if (doc == null) null else doc["channel"] as Long
    }

    fun setModLogChannel(id: String, channel: Long) {
        saveField(id, "modlogchannels", Document().append("channel", channel))
    }

    fun getColor(id: String): String? {
        val doc = getDoc(id, "colors")
        return if (doc == null) null else doc["color"] as String
    }

    fun setColor(id: String, color: String) {
        saveField(id, "colors", Document().append("color", color))
    }

    fun getLogTypeEnabled(id: String, type: String): Boolean? {
        val doc = getDoc(id, "log_types")
        return if (doc == null) null else doc[type] as Boolean?
    }

    fun setLogTypeEnabled(id: String, type: String, enabled: Boolean) {
        val doc = getDoc(id, "log_types") ?: Document()
        saveField(id, "log_types", doc.append(type, enabled))
    }

    fun getModRole(id: String): String? {
        val doc = getDoc(id, "mod_roles") ?: return null
        return doc["role"] as String?
    }

    fun setModRole(id: String, role: String) {
        saveField(id, "mod_roles", Document().append("role", role))
    }

    fun getOsuUser(id: String): String? {
        val doc = getDoc("global", "osu_users") ?: return null
        return doc[id] as String?
    }

    fun setOsuUser(id: String, user: String) {
        val doc = getDoc("global", "osu_users") ?: Document()
        saveField("global", "osu_users", doc.append(id, user))
    }

    private fun getDoc(id: String, collection: String): Document? {
        return database.getCollection(collection).find(Filters.eq("_id", id)).firstOrNull()
    }

    private fun saveField(id: String, collection: String, data: Document) {
        database.getCollection(collection).replaceOne(Filters.eq("_id", id), data.append("_id", id), updateOptions)
    }

    private fun removeField(id: String, collection: String) {
        database.getCollection(collection).deleteOne(Filters.eq("_id", id))
    }
}