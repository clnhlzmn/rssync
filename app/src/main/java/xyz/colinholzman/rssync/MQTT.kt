package xyz.colinholzman.rssync

import android.content.Context
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.eclipse.paho.android.service.MqttAndroidClient

class MQTT(val context: Context, val server: String, val port: String, val user: String?, val password: String?, val notify: () -> Unit) {

    private val broker = "tcp://$user:$password@$server:$port"
    private val clientId = context.getSharedPreferences("rssync", Context.MODE_PRIVATE).getString("client_id", null)
    private val client = MqttAndroidClient(context, broker, clientId)

    private var connected = false

    fun connect() {
        if (!connected) {
            val connOpts = MqttConnectOptions()
            connOpts.isCleanSession = true
            connOpts.userName = user
            connOpts.password = password?.toCharArray()

            client.connect(connOpts,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        connected = true
                        client.subscribe("rssync/#", 2) { topic, message ->
                            if (!topic.endsWith(clientId!!)) {
                                notify()
                            }
                        }
                    }
                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        connected = false
                    }
                }
            )
        }
    }

    fun publish() {
        if (connected)
            client.publish("rssync/$clientId", MqttMessage())
    }

    fun disconnect() {
        if (connected) {
            client.disconnect()
            connected = false
        }
    }

}