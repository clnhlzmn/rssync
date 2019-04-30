package xyz.colinholzman.rssync

import android.content.Context
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

    var actionButton: Button? = null

    var href: Uri? = null
    var token: String? = null

    private fun initState() {
        val prefs = getPreferences(Context.MODE_PRIVATE)
        this.token = prefs.getString("token", null)
        val hrefStr = prefs.getString("href", null)

        if (token == null && hrefStr != null) {
            href = Uri.parse(hrefStr)
            setStateConnected()
        } else {
            setStateDisconnected()
        }
    }

    private fun setStateDisconnected() {

        val inputText = findViewById<EditText>(R.id.editTextInput)

        href = null
        token = null

        val prefs = getPreferences(Context.MODE_PRIVATE).edit()
        prefs.remove("token")
        prefs.remove("href")
        prefs.apply()

        actionButton!!.text = "Connect"
        actionButton!!.setOnClickListener {
            Discovery.lookup(
                inputText.text.toString(),
                {
                    e -> Log.e(id, e)
                },
                { jrd ->
                    href = Authorization.getHref(jrd)
                    val authFragment =
                        AuthorizeFragment.newInstance(Authorization.getAuthQuery(jrd).toString())
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frame, authFragment)
                    transaction.commit()
                }
            )
        }

        val blankFragment = BlankFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, blankFragment)
        transaction.commit()

    }

    private fun setStateConnected() {

        val prefs = getPreferences(Context.MODE_PRIVATE).edit()
        prefs.putString("token", token)
        prefs.putString("href", href.toString())
        prefs.apply()

        actionButton!!.text = "Disconnect"
        actionButton!!.setOnClickListener {
            setStateDisconnected()
        }

        val connectedFragment = ConnectedFragment.newInstance(token!!)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, connectedFragment)
        transaction.commit()
        connectedFragment.rs = RemoteStorage(href!!, this.token!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        actionButton = findViewById(R.id.buttonLookup)

        initState()
    }

    override fun onAuthorization(token: String) {
        this.token = token
        setStateConnected()
    }

    override fun onPushClick() {

    }

    override fun onPullClick() {

    }

}
