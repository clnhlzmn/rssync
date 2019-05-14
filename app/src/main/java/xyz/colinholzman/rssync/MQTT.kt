package xyz.colinholzman.rssync

import android.content.Context
import android.util.Log
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MQTT(val context: Context, val server: String, val port: String, val user: String?, val password: String?, val notify: () -> Unit) {

//    private val broker = "tcp://$user:$password@$server:$port"
    private val broker = "tcp://$server:$port"
    private val clientId = context.getSharedPreferences("rssync", Context.MODE_PRIVATE).getString("client_id", null)
    private val client = MqttClient(broker, clientId, MemoryPersistence())

    private var connected = false

    fun connect() {
        if (!connected) {
            val connOpts = MqttConnectOptions()
            connOpts.isCleanSession = true
            connOpts.userName = user
            connOpts.password = password?.toCharArray()

            client.setCallback(
                object: MqttCallbackExtended {
                    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                        connected = true
                        client.subscribe("rssync/#", 0) { topic, message ->
                            if (!topic.endsWith(clientId!!)) {
                                notify()
                            }
                        }
                        Log.i("MQTT", "connected")
                    }
                    override fun messageArrived(topic: String?, message: MqttMessage?) {
                        Log.i("MQTT", "messageArrived")
                    }
                    override fun connectionLost(cause: Throwable?) {
                        connected = false
                        Log.i("MQTT", "connectionLost")
                    }
                    override fun deliveryComplete(token: IMqttDeliveryToken?) {
                        Log.i("MQTT", "deliveryComplete")
                    }
                }
            )
            client.connect(connOpts)
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