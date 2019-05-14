package xyz.colinholzman.rssync

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.util.Log
import android.widget.TextView
import xyz.colinholzman.remotestorage_kotlin.Authorization
import xyz.colinholzman.remotestorage_kotlin.Discovery
import xyz.colinholzman.remotestorage_kotlin.RemoteStorage
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        val id = "MainActivity"
    }

    var rssync: RsSync? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("rssync", Context.MODE_PRIVATE)
        if (!prefs.contains("client_id")) {
            val editor = prefs.edit()
            editor.putString("client_id", UUID.randomUUID().toString())
            editor.apply()
        }

        val rsUserField = findViewById<EditText>(R.id.editTextRsUser)
        rsUserField.setText(prefs.getString("user", "user@example.com"))

        val rsTokenField = findViewById<TextView>(R.id.textViewRsToken)
        rsTokenField.setText(prefs.getString("token", "***"))

        val mqttServerField = findViewById<EditText>(R.id.editTextMqttServer)
        mqttServerField.setText(prefs.getString("mqtt_server", "example.com"))

        val mqttPortField = findViewById<EditText>(R.id.editTextMqttPort)
        mqttPortField.setText(prefs.getString("mqtt_port", "12345"))

        val mqttUserField = findViewById<EditText>(R.id.editTextMqttUser)
        mqttUserField.setText(prefs.getString("mqtt_user", "user"))

        val mqttPasswordField = findViewById<EditText>(R.id.editTextMqttPassword)
        mqttPasswordField.setText(prefs.getString("mqtt_password", "password"))

        val authorizeButton = findViewById<Button>(R.id.buttonAuthorize)
        authorizeButton?.setOnClickListener {
            val intent = Intent(this, AuthorizeActivity::class.java).apply {
                putExtra(AuthorizeActivity.USER, rsUserField.text.toString())
            }
            startActivity(intent)
        }

        val startButton = findViewById<Button>(R.id.buttonStart)
        startButton?.setOnClickListener{
            rssync = RsSync(this)
            rssync?.start()
        }

        val saveButton = findViewById<Button>(R.id.buttonSave)
        saveButton?.setOnClickListener {
            val editor = getSharedPreferences("rssync", Context.MODE_PRIVATE).edit()
            editor.putString("user", rsUserField.text.toString())
            editor.putString("mqtt_server", mqttServerField.text.toString())
            editor.putString("mqtt_port", mqttPortField.text.toString())
            editor.putString("mqtt_user", mqttUserField.text.toString())
            editor.putString("mqtt_password", mqttPasswordField.text.toString())
            editor.apply()
        }
    }

}
