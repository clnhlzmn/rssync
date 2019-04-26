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

fun getUri(input: String): Uri {
    return Uri.parse(input)
}

class MainActivity : AppCompatActivity() {

    companion object {
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
