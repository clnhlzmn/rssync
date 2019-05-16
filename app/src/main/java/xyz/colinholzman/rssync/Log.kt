package xyz.colinholzman.rssync

import java.util.*
import kotlin.collections.ArrayList

class Log {
    companion object {
        val calendar = Calendar.getInstance()
        val log = ArrayList<String>()
        val listeners = ArrayList<(String)->Unit>()
        fun println(value: String) {
            log.add("${calendar.time}: $value")
            listeners.forEach { it.invoke(value) }
        }
    }
}