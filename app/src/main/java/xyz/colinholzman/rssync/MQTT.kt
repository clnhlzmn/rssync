package xyz.colinholzman.rssync

import android.content.Context
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MQTT(val context: Context, val server: String, val port: String, val user: String?, val password: String?, val notify: () -> Unit) {

    private val broker = "tcp://$server:$port"
    private val clientId = context.getSharedPreferences("rssync", Context.MODE_PRIVATE).getString("client_id", null)
    private val client = MqttClient(broker, clientId, MemoryPersistence())

    fun connect() {
        val connOpts = MqttConnectOptions()
        connOpts.isCleanSession = true
        connOpts.userName = user
        connOpts.password = password?.toCharArray()
        client.connect(connOpts)

        client.subscribe("rssync/#") { topic, _ ->
            if (!topic.endsWith(clientId!!)) {
                notify()
            }
        }
    }

    fun publish() {
        client.publish("rssync/$clientId", MqttMessage())
    }

    fun disconnect() {
        client.disconnect()
    }

}