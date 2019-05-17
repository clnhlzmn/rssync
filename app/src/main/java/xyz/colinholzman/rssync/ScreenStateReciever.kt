package xyz.colinholzman.rssync

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log

class ScreenStateReciever : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null && intent.action != null) {
            if (intent.action == Intent.ACTION_SCREEN_ON) {
                Log.i("ScreenStateReciever", "ACTION_SCREEN_ON")
            } else if (intent.action == Intent.ACTION_SCREEN_OFF) {
                Log.i("ScreenStateReciever", "ACTION_SCREEN_OFF")
                //stop foreground
                val fgServiceIntent = Intent(context, ForegroundService::class.java)
                fgServiceIntent.action = ForegroundService.ACTION_STOP_FOREGROUND_SERVICE
                context.startService(fgServiceIntent)
            } else if (intent.action == Intent.ACTION_USER_PRESENT) {
                Log.i("ScreenStateReciever", "ACTION_USER_PRESENT")
                //start foreground
                val fgServiceIntent = Intent(context, ForegroundService::class.java)
                fgServiceIntent.action = ForegroundService.ACTION_START_FOREGROUND_SERVICE
                context.startService(fgServiceIntent)
            }
        }
    }
}