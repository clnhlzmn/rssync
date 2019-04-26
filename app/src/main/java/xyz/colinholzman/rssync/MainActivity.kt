package xyz.colinholzman.rssync

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import okhttp3.*
import java.io.IOException
import java.net.URL
import java.net.URLConnection
import android.content.Intent
import com.google.gson.Gson

class Link {
    val rel: String? = null
    val type: String? = null
    val href: String? = null
    val titles: Map<String, Any>? = null
    val properties: Map<String, Any?>? = null
}

class JRD {
    val expires: String? = null
    val subject: String? = null
    val aliases: Array<String>? = null
    val properties: Map<String, Any?>? = null
    val links: Array<Link>? = null
}

fun getUri(input: String): Uri {
    return Uri.parse(input)
}

class MainActivity : AppCompatActivity() {

    companion object {
        var gson = Gson()
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

            val query = Uri.Builder()
            query.scheme("https")
                .authority(inputUri.host)
                .appendPath(".well-known")
                .appendPath("webfinger")
                .appendQueryParameter("resource", "acct:${inputUri.authority}")

            val request = Request.Builder().method("GET", null).url(query.toString()).build()

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

                                val result = gson.fromJson(resultText.text.toString(), JRD().javaClass)

//                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse())
//                                startActivity(browserIntent)

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
