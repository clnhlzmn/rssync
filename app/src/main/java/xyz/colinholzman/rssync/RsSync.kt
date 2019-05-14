package xyz.colinholzman.rssync

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import xyz.colinholzman.remotestorage_kotlin.RemoteStorage

class RsSync(val context: Context) {

    private var rs = RemoteStorage("href", "token")
    private var mqtt = MQTT(context,"", "", "", "") {}

    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    private fun getServerContent(): String? {
        return rs.getSync("/clipboard/txt")
    }

    private fun setServerContent(value: String?) {
        if (value != null) {
            rs.putSync("/clipboard/txt", value)
        } else {
            rs.deleteSync("/clipboard/txt")
        }
    }

    fun start() {

        val prefs = context.getSharedPreferences("rssync", Context.MODE_PRIVATE)

        val href = prefs.getString("href", null)
        val token = prefs.getString("token", null)
        if (href != null && token != null)
            rs = RemoteStorage(href, token)

        val server = prefs.getString("mqtt_server", null)
        val port = prefs.getString("mqtt_port", null)
        val user = prefs.getString("mqtt_user", null)
        val pass = prefs.getString("mqtt_password", null)
        if (server != null && port != null && user != null && pass != null) {
            mqtt = MQTT(context, server, port, user, pass) {
                val content = getServerContent()
                Log.i("RsSync", "remote changed: $content")
                clipboard.primaryClip?.addItem(ClipData.Item(content))
            }
        }

        mqtt.connect()

        clipboard.addPrimaryClipChangedListener {
            val content = clipboard.primaryClip?.getItemAt(0)?.text.toString()
            println("local changed: $content")
            setServerContent(content)
            mqtt.publish()
        }

    }

    fun stop() {

    }

}