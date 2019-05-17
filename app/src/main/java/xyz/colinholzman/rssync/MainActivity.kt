package xyz.colinholzman.rssync

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import xyz.colinholzman.remotestorage_kotlin.Authorization
import xyz.colinholzman.remotestorage_kotlin.Discovery
import xyz.colinholzman.remotestorage_kotlin.RemoteStorage
import java.util.*
import kotlin.math.max
import kotlin.math.min


class MainActivity : AppCompatActivity() {

//    var rssync: RsSync? = null

    companion object {
        val id = "MainActivity"
    }

    private fun updatePrefs(key: String, value: String) {
        val editor = getSharedPreferences("rssync", Context.MODE_PRIVATE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("rssync", Context.MODE_PRIVATE)
        val rsTokenField = findViewById<TextView>(R.id.textViewRsToken)
        rsTokenField.text = prefs.getString("token", "***")
    }

    @SuppressLint("BatteryLife")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent =
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                .setData(Uri.parse("package:" + packageName))

        startActivity(intent)

        val prefs = getSharedPreferences("rssync", Context.MODE_PRIVATE)
        if (!prefs.contains("client_id")) {
            val editor = prefs.edit()
            editor.putString("client_id", UUID.randomUUID().toString())
            editor.apply()
        }

        val rsUserField = findViewById<EditText>(R.id.editTextRsUser)
        rsUserField.setText(prefs.getString("user", "user@example.com"))
        rsUserField.addTextChangedListener(AfterTextChangedListener{
            updatePrefs("user", it)
        })

        val rsTokenField = findViewById<TextView>(R.id.textViewRsToken)
        rsTokenField.text = prefs.getString("token", "***")

        val mqttServerField = findViewById<EditText>(R.id.editTextMqttServer)
        mqttServerField.setText(prefs.getString("mqtt_server", "example.com"))
        mqttServerField.addTextChangedListener(AfterTextChangedListener{
            updatePrefs("mqtt_server", it)
        })

        val mqttPortField = findViewById<EditText>(R.id.editTextMqttPort)
        mqttPortField.setText(prefs.getString("mqtt_port", "12345"))
        mqttPortField.addTextChangedListener(AfterTextChangedListener{
            updatePrefs("mqtt_port", it)
        })

        val mqttUserField = findViewById<EditText>(R.id.editTextMqttUser)
        mqttUserField.setText(prefs.getString("mqtt_user", "user"))
        mqttUserField.addTextChangedListener(AfterTextChangedListener{
            updatePrefs("mqtt_user", it)
        })

        val mqttPasswordField = findViewById<EditText>(R.id.editTextMqttPassword)
        mqttPasswordField.setText(prefs.getString("mqtt_password", "password"))
        mqttPasswordField.addTextChangedListener(AfterTextChangedListener{
            updatePrefs("mqtt_password", it)
        })

        val authorizeButton = findViewById<Button>(R.id.buttonAuthorize)
        authorizeButton?.setOnClickListener {
            val intent = Intent(this, AuthorizeActivity::class.java).apply {
                putExtra(AuthorizeActivity.USER, rsUserField.text.toString())
            }
            startActivity(intent)
        }

        val log = findViewById<EditText>(R.id.editTextLog)

        Log.listeners.add {
            val lines = min(Log.log.size, 10)
            log.setText(Log.log.takeLast(lines).reduce { acc, s -> "$acc\n$s" },  TextView.BufferType.EDITABLE)
        }

        val fgServiceIntent = Intent(this@MainActivity, ForegroundService::class.java)
        fgServiceIntent.action = ForegroundService.ACTION_START_FOREGROUND_SERVICE
        startService(fgServiceIntent)

//        rssync = RsSync(this)
//        rssync?.start()

    }

}
