package xyz.colinholzman.rssync

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.util.Log
import xyz.colinholzman.remotestorage_kotlin.Authorization
import xyz.colinholzman.remotestorage_kotlin.Discovery
import xyz.colinholzman.remotestorage_kotlin.RemoteStorage


class MainActivity : AppCompatActivity() {

    companion object {
        val id = "MainActivity"
    }

    var authorizeButton: Button? = null
    var startButton: Button? = null
    var saveButton: Button? = null

    var href: String? = null
    var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rsUserField = findViewById<EditText>(R.id.editTextRsUser)
        authorizeButton = findViewById(R.id.buttonAuthorize)
        authorizeButton?.setOnClickListener {
            val intent = Intent(this, AuthorizeActivity::class.java).apply {
                putExtra(AuthorizeActivity.USER, rsUserField.text.toString())
            }
            startActivity(intent)
        }
        startButton = findViewById(R.id.buttonStart)
        saveButton = findViewById(R.id.buttonSave)
    }

}
