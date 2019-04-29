package xyz.colinholzman.rssync

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE

class Clipboard {
    companion object {
        fun getContent(ctx: Context): String? {
            var txt: String? = null
            val clipboard: ClipboardManager? = ctx.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            if (clipboard != null) {
                if (clipboard.hasPrimaryClip()) {
                    txt = clipboard.primaryClip!!.getItemAt(0).text.toString()
                }
            }
            return txt
        }
        fun setContent(ctx: Context, value: String) {
            val clipboard: ClipboardManager? = ctx.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            if (clipboard != null) {
                clipboard.primaryClip = ClipData.newPlainText("/clipboard/txt", value)
            }
        }
    }
}