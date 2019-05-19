package xyz.colinholzman.rssync

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import xyz.colinholzman.remotestorage_kotlin.RemoteStorage
import java.util.*

class RsSync(val context: Context) {

    private var started = false

    private var rs = RemoteStorage("href", "token")

    private val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    private var clipChangedListener: ClipboardManager.OnPrimaryClipChangedListener? = null

    private var remoteListener: ChangeListener? = null

    private fun setLocalContent(value: String?) {
        clipboard.removePrimaryClipChangedListener(clipChangedListener)
        clipboard.primaryClip = ClipData.newPlainText("/clipboard/txt", value)
        clipboard.addPrimaryClipChangedListener(clipChangedListener)
    }

    private fun initAndStartRemoteListener() {
        val prefs = context.getSharedPreferences("rssync", Context.MODE_PRIVATE)
        val href = prefs.getString("href", null)
        val token = prefs.getString("token", null)
        if (href != null && token != null) {
            //create instance with saved values
            rs = RemoteStorage(href, token)
            //attempt to get initial value
            rs.get(
                path = "/clipboard/txt",
                onFailure = {
                    Log.i("RsSync","unable to connect to server, retrying")
                    Timer().schedule(
                        object : TimerTask() {
                            override fun run() { initAndStartRemoteListener() }
                        },
                        10000
                    )
                },
                onSuccess = { initialValue ->
                    //got initial value, use to set up listener
                    remoteListener = ChangeListener(
                        updatePeriod = 10000,
                        current = initialValue,
                        getter = { success: (String?) -> Unit, fail: () -> Unit ->
                            rs.get(
                                path = "/clipboard/txt",
                                onFailure = { fail() },
                                onSuccess = { success(it) }
                            )
                        },
                        listener = {
                            Log.i("RsSync","remote changed: $it")
                            setLocalContent(it)
                        }
                    )
                    remoteListener!!.start()
                }
            )
        }
    }

    private fun setServerContent(value: String?) {
        if (value!= null) {
            rs.put(
                path = "/clipboard/txt",
                value = value,
                onFailure = {},
                onSuccess = {}
            )
        } else {
            rs.delete(
                path = "/clipboard/txt",
                onFailure = {},
                onSuccess = {}
            )
        }
    }

    fun start() {

        if (!started) {

            initAndStartRemoteListener()

            clipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
                AsyncTask.execute {
                    val content = clipboard.primaryClip?.getItemAt(0)?.text.toString()
                    Log.i("RsSync", "local changed: $content")
                    setServerContent(content)
                    remoteListener?.current = content
                }
            }

            clipboard.addPrimaryClipChangedListener(clipChangedListener)

            started = true
        }

    }

    fun stop() {
        if (started) {
            clipboard.removePrimaryClipChangedListener(clipChangedListener)
            started = false
        }
    }

}