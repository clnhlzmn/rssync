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
import android.webkit.WebResourceRequest
import android.webkit.WebView
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import android.webkit.WebViewClient



class MainActivity : AppCompatActivity() {

    companion object {
        var gson = Gson()
        val id = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputText = findViewById<EditText>(R.id.editTextInput)
        val resultText = findViewById<TextView>(R.id.textViewResult)
        val button = findViewById<Button>(R.id.buttonLookup)

        val webView = findViewById<WebView>(R.id.webView)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                //here, when we see the redirect url, we can take the authorization token from the fragment
                view.loadUrl(request.url.toString())
                return false // then it is not handled by default action
            }
        }

        button.setOnClickListener {
            Discovery.lookup(
                inputText.text.toString(),
                { e ->
                    Log.e(id, e.toString())
                    runOnUiThread {
                        resultText.text = e.toString()
                    }
                },
                { response ->
                    runOnUiThread {
                        if (response.code() == 200) {
                            resultText.text = response.body()?.string()

                            val result = gson
                                .fromJson<JSONResourceDescriptor>(
                                    resultText.text.toString(),
                                    object: TypeToken<JSONResourceDescriptor>(){}.type
                                )

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

                            webView.loadUrl(authQuery.toString())

                            Log.i("result", result.toString())


                        } else {
                            resultText.text = "failure: ${response.code()}"
                        }
                    }
                }
            )
        }
    }
}
