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

    fun connect() {
        if (!client.isConnected) {
            try {
                val connOpts = MqttConnectOptions()
                connOpts.isCleanSession = true
                connOpts.userName = user
                connOpts.password = password?.toCharArray()

                client.setCallback(
                    object : MqttCallbackExtended {
                        override fun connectComplete(reconnect: Boolean, serverURI: String?) {
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
                            Log.i("MQTT", "connectionLost")
                        }

                        override fun deliveryComplete(token: IMqttDeliveryToken?) {
                            Log.i("MQTT", "deliveryComplete")
                        }
                    }
                )
                client.connect(connOpts)
            } catch (e: MqttException) {
                Log.e("MQTT", e.toString())
            }
        }
    }

    fun publish() {
        try {
            client.publish("rssync/$clientId", MqttMessage())
        } catch (e: MqttException) {
            Log.e("MQTT", e.toString())
        }
    }

    fun disconnect() {
        try {
            client.disconnect()
        } catch (e: MqttException) {
            Log.e("MQTT", e.toString())
        }
    }

}