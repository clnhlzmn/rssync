package xyz.colinholzman.rssync

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import okhttp3.*
import java.io.IOException
import android.content.Intent
import android.util.Log
import android.webkit.URLUtil
import com.google.gson.*
import com.google.gson.reflect.TypeToken

class Link {
    val rel: String = "rel"
    val type: String? = null
    val href: String? = null
    val titles: Map<String, String>? = null
    val properties: Map<String, String?>? = null
}

class JRD {
    val expires: String? = null
    val subject: String = "subject"
    val aliases: Array<String>? = null
    val properties: Map<String, Any?>? = null
    val links: Array<Link>? = null
}

fun getUri(input: String): Uri {
    return Uri.parse(URLUtil.guessUrl(input))
}

class MainActivity : AppCompatActivity() {

    companion object {
        var gson = Gson()
//            GsonBuilder()
//                    .registerTypeAdapter(Properties::class.java, PropertiesDeserializer())
//                    .create()
        val client = OkHttpClient()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputText = findViewById<EditText>(R.id.editTextInput)
        val resultText = findViewById<TextView>(R.id.textViewResult)
        val button = findViewById<Button>(R.id.buttonLookup)

        button.setOnClickListener {
            val inputUri = getUri(inputText.text.toString())

            val webfingerQuery = Uri.Builder()
            webfingerQuery.scheme("https")
                .authority(inputUri.host)
                .appendPath(".well-known")
                .appendPath("webfinger")
                .appendQueryParameter("resource", "acct:${inputUri.authority}")

            val request = Request.Builder().method("GET", null).url(webfingerQuery.toString()).build()

            client.newCall(request).enqueue(
                object: Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            resultText.text = "failure"
                        }
                    }
                    override fun onResponse(call: Call, response: Response) {
                        runOnUiThread {
                            if (response.code() == 200) {

                                resultText.text = response.body()?.string()

                                val result = gson.fromJson<JRD>(resultText.text.toString(), object: TypeToken<JRD>(){}.type)

                                val href = result.links?.get(0)?.href
                                val authUrlString = result.links?.get(0)?.properties?.get("http://tools.ietf.org/html/rfc6749#section-4.2")
                                val version = result.links?.get(0)?.properties?.get("http://remotestorage.io/spec/version")

                                val authUri = Uri.parse(authUrlString)
                                val authQuery =
                                    Uri.Builder()
                                        .scheme(authUri.scheme)
                                        .authority(authUri.authority)
                                        .path(authUri.path)
                                        .fragment(authUri.fragment)
                                        .appendQueryParameter("client_id", "colinholzman.xyz")
                                        .appendQueryParameter("response_type", "token")
                                        .appendQueryParameter("redirect_uri", "https://example.com")
                                        .appendQueryParameter("scope", "*:rw")
                                        .build()

                                val browserIntent = Intent(Intent.ACTION_VIEW, authQuery)
                                startActivity(browserIntent)

                                Log.i("result", result.toString())


                            } else {
                                resultText.text = "failure: ${response.code()}"
                            }
                        }
                    }
                }
            )
        }
    }

}
