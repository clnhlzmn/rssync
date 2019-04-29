package xyz.colinholzman.rssync

import android.net.Uri
import android.webkit.URLUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException

class Discovery {

    companion object {
        var gson = Gson()
        val client = OkHttpClient()

        fun lookup(userAddress: String, onFailure: (String) -> Unit, onSuccess: (JSONResourceDescriptor) -> Unit) {
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
                        onFailure(e.toString())
                    }
                    override fun onResponse(call: Call, response: Response) {
                        if (response.code() == 200) {
                            val result =
                                gson.fromJson<JSONResourceDescriptor>(
                                    response.body()?.string(),
                                    object: TypeToken<JSONResourceDescriptor>(){}.type
                                )
                            onSuccess(result)
                        } else {
                            onFailure(response.code().toString())
                        }
                    }
                }
            )
        }

    }

}