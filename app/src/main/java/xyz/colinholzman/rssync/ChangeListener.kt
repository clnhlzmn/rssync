package xyz.colinholzman.rssync

import java.util.*

class ChangeListener(
    val updatePeriod: Long,
    var current: String?,
    val getter: ((String?)->Unit, ()->Unit)->Unit,
    val listener: (String?)->Unit) {

    private var started = false

    private var onSuccess: ((String?)->Unit)? = null
    private var onFail: (()->Unit)? = null

    private fun schedule() {
        if (started) {
            Timer().schedule(
                object: TimerTask() {
                    override fun run() {
                        getter(onSuccess!!, onFail!!)
                    }
                },
                updatePeriod
            )
        }
    }

    init {
        onSuccess = {
            if (it != current) {
//                println("ChangeListener got new value $it")
                current = it
                listener(it)
            }
            schedule()
        }
        onFail = {
            println("ChangeListener failed to get value, retrying")
            schedule()
        }
    }

    fun start() {
        if (!started) {
            started = true
            getter(onSuccess!!, onFail!!)
        }
    }

    fun stop() {
        if (started) {
            started = false
        }
    }

}