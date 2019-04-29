package xyz.colinholzman.rssync

import android.net.Uri
import android.webkit.URLUtil
import okhttp3.*
import java.io.IOException

class Discovery {

    companion object {
        val client = OkHttpClient()

        fun lookup(userAddress: String, onFailure: (IOException) -> Unit, onSuccess: (Response) -> Unit) {
            val userAddressUri = Uri.parse(URLUtil.guessUrl(userAddress))
            val webfingerQueryUri = Uri.Builder()
            webfingerQueryUri.scheme("https")
                .authority(userAddressUri.host)
                .appendPath(".well-known")
                .appendPath("webfinger")
                .appendQueryParameter("resource", "acct:${userAddressUri.authority}")
            val webfingerQueryRequest = Request.Builder()
                .method("GET", null)
                .url(webfingerQueryUri.toString())
                .build()
            client.newCall(webfingerQueryRequest).enqueue(
                object: Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        onFailure(e)
                    }
                    override fun onResponse(call: Call, response: Response) {
                        onSuccess(response)
                    }
                }
            )
        }

    }

}