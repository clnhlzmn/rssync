package xyz.colinholzman.rssync

import android.content.Context
import android.os.AsyncTask
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MQTT(val context: Context, val server: String, val port: String, val user: String?, val password: String?, val notify: () -> Unit) {

//    private val broker = "tcp://$user:$password@$server:$port"
    private val broker = "tcp://$server:$port"
    private val clientId = context.getSharedPreferences("rssync", Context.MODE_PRIVATE).getString("client_id", null)
    private val client = MqttAndroidClient(context, broker, clientId)

    fun connect() {
        if (!client.isConnected) {
            try {
                val connOpts = MqttConnectOptions()
                connOpts.isCleanSession = true
                connOpts.userName = user
                connOpts.password = password?.toCharArray()
//                connOpts.keepAliveInterval = 10 * 60

                client.setCallback(
                    object : MqttCallbackExtended {
                        override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                            client.subscribe("rssync/#", 0) { topic, message ->
                                if (!topic.endsWith(clientId!!)) {
                                    notify()
                                }
                            }
                            Log.println("[MQTT]: connected to $broker")
                        }

                        override fun messageArrived(topic: String?, message: MqttMessage?) {
                            Log.println("[MQTT]: messageArrived")
                        }

                        override fun connectionLost(cause: Throwable?) {
                            Log.println("[MQTT]: connectionLost")
                            Log.println("[MQTT]: connecting to $broker")
                            client.connect()
                        }

                        override fun deliveryComplete(token: IMqttDeliveryToken?) {
                            Log.println("[MQTT]: deliveryComplete")
                        }
                    }
                )
                Log.println("[MQTT]: connecting to $broker")
                client.connect(connOpts)
            } catch (e: MqttException) {
                Log.println("[MQTT]: $e")
            }
        }
    }

    fun publish() {
        if (client.isConnected) {
            try {
                client.publish("rssync/$clientId", MqttMessage())
            } catch (e: MqttException) {
                Log.println("[MQTT]: $e")
            }
        }
    }

    fun disconnect() {
        try {
            client.disconnect()
        } catch (e: MqttException) {
            Log.println("[MQTT]: $e")
        }
    }

}