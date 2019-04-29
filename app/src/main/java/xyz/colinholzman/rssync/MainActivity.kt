package xyz.colinholzman.rssync

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputText = findViewById<EditText>(R.id.editTextInput)
        val connectButton = findViewById<Button>(R.id.buttonLookup)

        //TODO: determine connection status and set fragment accordingly
        connectButton.setOnClickListener {
            Discovery.lookup(
                inputText.text.toString(),
                { e -> Log.e(id, e) },
                { jrd ->
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
        //TODO: save token
        val connectedFragment = ConnectedFragment.newInstance(token)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, connectedFragment)
        transaction.commit()
        //TODO: change connect to disconnect
    }

}
