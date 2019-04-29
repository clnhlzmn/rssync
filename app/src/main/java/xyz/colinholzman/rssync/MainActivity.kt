package xyz.colinholzman.rssync

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import android.webkit.WebViewClient



class MainActivity : AppCompatActivity(), AuthorizeFragment.OnAuthorizationListener {

    companion object {
        val id = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputText = findViewById<EditText>(R.id.editTextInput)
        val connectButton = findViewById<Button>(R.id.buttonLookup)

        connectButton.setOnClickListener {
            Discovery.lookup(
                inputText.text.toString(),
                { e -> Log.e(id, e.toString()) },
                { jrd ->
                    val authFragment = AuthorizeFragment.newInstance(Authorization.getAuthQuery(jrd).toString())
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frame, authFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            )
        }
    }

    override fun onAuthorization(token: String) {
        Log.i(id, token)
    }

}
