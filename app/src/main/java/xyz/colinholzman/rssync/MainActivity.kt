package xyz.colinholzman.rssync

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.util.Log


class MainActivity : AppCompatActivity(),
    AuthorizeFragment.OnAuthorizationListener,
    ConnectedFragment.OnConnectedInteractionListener {

    companion object {
        val id = "MainActivity"
    }

    var href: Uri? = null
    var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputText = findViewById<EditText>(R.id.editTextInput)
        val connectButton = findViewById<Button>(R.id.buttonLookup)

        //TODO: determine connection status and set fragment accordingly
        connectButton.setOnClickListener {
            Discovery.lookup(
                inputText.text.toString(),
                {
                    e -> Log.e(id, e)
                },
                { jrd ->
                    this.href = Authorization.getHref(jrd)
                    val authFragment = AuthorizeFragment.newInstance(Authorization.getAuthQuery(jrd).toString())
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frame, authFragment)
                    transaction.commit()
                }
            )
        }
    }

    override fun onAuthorization(token: String) {
        Log.i(id, token)

        val prefs = getPreferences(Context.MODE_PRIVATE).edit()
        prefs.putString("token", token)
        prefs.apply()

        val connectedFragment = ConnectedFragment.newInstance(token)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, connectedFragment)
        transaction.commit()

        this.token = token
        connectedFragment.rs = RemoteStorage(href!!, this.token!!)
        //TODO: change connect to disconnect
    }

    override fun onPushClick() {

    }

    override fun onPullClick() {

    }

}
