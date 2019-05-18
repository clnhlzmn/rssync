package xyz.colinholzman.rssync

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.*
import kotlin.math.min
import android.content.IntentFilter

class MainActivity : AppCompatActivity() {

    val ssr = ScreenStateReciever()

    companion object {
        val id = "MainActivity"
    }

    private fun updatePrefs(key: String, value: String) {
        val editor = getSharedPreferences("rssync", Context.MODE_PRIVATE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("rssync", Context.MODE_PRIVATE)
        val rsTokenField = findViewById<TextView>(R.id.textViewRsToken)
        rsTokenField.text = prefs.getString("token", "***")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lockFilter = IntentFilter()
        lockFilter.addAction(Intent.ACTION_SCREEN_ON)
        lockFilter.addAction(Intent.ACTION_SCREEN_OFF)
        lockFilter.addAction(Intent.ACTION_USER_PRESENT)
        registerReceiver(ssr, lockFilter)

        val prefs = getSharedPreferences("rssync", Context.MODE_PRIVATE)

        val rsUserField = findViewById<EditText>(R.id.editTextRsUser)
        rsUserField.setText(prefs.getString("user", "user@example.com"))
        rsUserField.addTextChangedListener(AfterTextChangedListener{
            updatePrefs("user", it)
        })

        val rsTokenField = findViewById<TextView>(R.id.textViewRsToken)
        rsTokenField.text = prefs.getString("token", "***")

        val authorizeButton = findViewById<Button>(R.id.buttonAuthorize)
        authorizeButton?.setOnClickListener {
            val intent = Intent(this, AuthorizeActivity::class.java).apply {
                putExtra(AuthorizeActivity.USER, rsUserField.text.toString())
            }
            startActivity(intent)
        }

        //start foreground
        val fgServiceIntent = Intent(this, ForegroundService::class.java)
        fgServiceIntent.action = ForegroundService.ACTION_START_FOREGROUND_SERVICE
        startService(fgServiceIntent)

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(ssr)
    }

}
