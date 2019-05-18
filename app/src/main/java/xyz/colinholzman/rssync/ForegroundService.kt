package xyz.colinholzman.rssync

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.widget.Toast
import android.app.PendingIntent
import android.os.AsyncTask
import android.util.Log


class ForegroundService : Service() {

    var rssync: RsSync? = null

    companion object {
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTION_PULL = "ACTION_PULL"
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }/* Used to build and start foreground service. */

    override fun onCreate() {
        super.onCreate()
        Log.d("ForegroundService", "My foreground service onCreate().")
        rssync = RsSync(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                ACTION_START_FOREGROUND_SERVICE -> {
                    Toast.makeText(applicationContext, "Foreground service is started.", Toast.LENGTH_LONG).show()
                    startForegroundService()
                    rssync?.start()
                }
                ACTION_STOP_FOREGROUND_SERVICE -> {
                    Toast.makeText(applicationContext, "Foreground service is stopped.", Toast.LENGTH_LONG).show()
                    stopForegroundService()
                    rssync?.stop()
                }
                ACTION_PULL -> {
                    Toast.makeText(applicationContext, "pull from storage", Toast.LENGTH_LONG).show()
                    rssync?.pull()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        Log.d("ForegroundService", "Start foreground service.")

        // Create notification builder.
        val builder = NotificationCompat.Builder(this, createNotificationChannel("RsSync", "RsSync"))

        //set small icon
        builder.setSmallIcon(R.mipmap.ic_launcher)

        //set title
        builder.setContentTitle("RsSync")
        builder.setContentText("click to pull")

        builder.setWhen(System.currentTimeMillis())
        // Make the notification max priority.
        builder.priority = NotificationManager.IMPORTANCE_HIGH

        //Create intent to pull from storage
        val pullIntent = Intent(this, ForegroundService::class.java)
        pullIntent.action = ACTION_PULL
        val pendingPullIntent = PendingIntent.getService(this, 0, pullIntent, 0)

        builder.setContentIntent(pendingPullIntent)

        // Build the notification.
        val notification = builder.build()

        // Start foreground service.
        startForeground(1, notification)
    }

    private fun stopForegroundService() {
        Log.d("ForegroundService", "Stop foreground service.")
        // Stop foreground service and remove the notification.
        stopForeground(true)
        // Stop the foreground service.
        stopSelf()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

}
