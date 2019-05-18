package xyz.colinholzman.rssync

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import xyz.colinholzman.remotestorage_kotlin.RemoteStorage

class RsSync(val context: Context) {

    private var started = false

    private var rs = RemoteStorage("href", "token")

    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    var clipChangedListener: ClipboardManager.OnPrimaryClipChangedListener? = null

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

        if (!started) {
            val prefs = context.getSharedPreferences("rssync", Context.MODE_PRIVATE)

            val href = prefs.getString("href", null)
            val token = prefs.getString("token", null)
            if (href != null && token != null)
                rs = RemoteStorage(href, token)

            clipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
                AsyncTask.execute {
                    val content = clipboard.primaryClip?.getItemAt(0)?.text.toString()
                    Log.i("RsSync", "local changed: $content")
                    setServerContent(content)
                }
            }

            clipboard.addPrimaryClipChangedListener(clipChangedListener)

            started = true
        }

    }

    fun pull() {
        if (started) {
            AsyncTask.execute {
                val content = getServerContent()
                Log.i("RsSync", "pulled: $content")
                clipboard.removePrimaryClipChangedListener(clipChangedListener)
                clipboard.primaryClip = ClipData.newPlainText("/clipboard/txt", content)
                clipboard.addPrimaryClipChangedListener(clipChangedListener)
            }
        }
    }

    fun stop() {
        if (started) {
            clipboard.removePrimaryClipChangedListener(clipChangedListener)
            started = false
        }
    }

}