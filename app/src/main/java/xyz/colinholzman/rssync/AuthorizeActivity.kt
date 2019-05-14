package xyz.colinholzman.rssync

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

import kotlinx.android.synthetic.main.activity_authorize.*
import xyz.colinholzman.remotestorage_kotlin.Authorization
import xyz.colinholzman.remotestorage_kotlin.Discovery
import xyz.colinholzman.rssync.MainActivity.Companion.id

class AuthorizeActivity : AppCompatActivity() {

    companion object {
        const val USER = "USER"
    }

    private class AuthTokenWebViewClient(
        val redirectUrl: String,
        val listener: OnAuthorizationListener?
    ): WebViewClient() {
        interface OnAuthorizationListener {
            fun onAuthorizationGranted(token: String)
            fun onAuthorizationDenied(reason: String)
        }
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            Log.i("AuthTokenWebViewClient", request.url.toString())
            if (request.url.toString().startsWith(redirectUrl)) {
                val token = request.url.fragment?.removePrefix("access_token=")
                val error = request.url.fragment?.removePrefix("error=")
                if (token != null && token != request.url.fragment) {
                    listener?.onAuthorizationGranted(token)
                    return true
                } else if (error != null && error != request.url.fragment) {
                    listener?.onAuthorizationDenied(error)
                    return true
                } else {
                    listener?.onAuthorizationDenied("unknown error")
                    return true
                }
            }
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorize)
        val webView = findViewById<WebView>(R.id.webView)
        val user = intent.getStringExtra(USER)
        Discovery.lookup(
            user,
            { finish() },
            {
                runOnUiThread {
                    val authUrl = Authorization.getAuthQuery(it)
                    webView.webViewClient =
                        AuthTokenWebViewClient(
                            Authorization.redirectUrl,
                            object: AuthTokenWebViewClient.OnAuthorizationListener {
                                override fun onAuthorizationGranted(token: String) {
                                    val prefs = getSharedPreferences("rssync", Context.MODE_PRIVATE)
                                    val editor = prefs.edit()
                                    editor.putString("token", token)
                                    editor.apply()
                                    Log.i("AuthorizeActivity", "authorized: $token")
                                    finish()
                                }
                                override fun onAuthorizationDenied(reason: String) {
                                    Log.i("AuthorizeActivity", "denied")
                                    finish()
                                }
                            }
                        )
                    webView.loadUrl(authUrl)
                }
            }
        )
    }

}
