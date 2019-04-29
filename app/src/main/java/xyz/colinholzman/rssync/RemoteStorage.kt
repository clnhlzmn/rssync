package xyz.colinholzman.rssync

import android.net.Uri
import okhttp3.*
import java.io.IOException

class RemoteStorage(
    var href: Uri,
    var token: String
) {

    fun put(path: String, value: String, onFailure: (String) -> Unit, onSuccess: (String) -> Unit) {
        Http.client.newCall(
            Request.Builder()
                .url("$href$path")
                .method("PUT", RequestBody.create(MediaType.parse("text/plain"), value))
                .addHeader("Authorization", "Bearer $token")
                .build()
        ).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.code() in 200..299)
                    onSuccess(response.code().toString())
                else
                    onFailure(response.code().toString())
            }
        })
    }

    fun get(path: String, onFailure: (String) -> Unit, onSuccess: (String) -> Unit) {Http.client.newCall(
        Request.Builder()
            .url("$href$path")
            .method("GET", null)
            .addHeader("Authorization", "Bearer $token")
            .build()
        ).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.code() in 200..299)
                    onSuccess(response.body()!!.string())
                else
                    onFailure(response.code().toString())
            }
        })
    }

    fun delete(path: String, onFailure: (String) -> Unit, onSuccess: (String) -> Unit) {

    }

}