package subscriber

import constants.ApplicationConstants
import io.reactivex.rxjava3.core.Single
import model.Connection
import java.io.DataInputStream
import java.lang.Exception
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.*

class SubscriberSocket(private var title: String) {

    private lateinit var socket: Socket

    fun connect(address: String, port: Int): Single<Boolean> {
        socket = Socket(address, port)
        println("Connecting to Subscriber socket")
        return try {
            Single.create {
                it.onSuccess(if (socket.isConnected)
                    true.apply {
                        subscribe()
                        beginReceiving()
                        println("Subscriber connected") }
                else false.apply { println("Subscriber not connected") })
            }
        } catch (exception: Exception) {
            println("Error occurred while connecting: ${exception.message}")
            Single.create { it.onError(exception) }
        }

    }

    private fun subscribe() {
        val subscriberPayloadBytes = "${ApplicationConstants.SUBSCRIBE}$title".toByteArray(StandardCharsets.UTF_8)
        send(subscriberPayloadBytes)
    }

    private fun beginReceiving(): Single<ByteArray> {
        val connection = Connection(socket = socket)
        val socket = connection.socket
        val bufferSize = socket?.receiveBufferSize
        return Single.create {
            try {
                val dataInputStream = DataInputStream(socket?.getInputStream()!!)
                val responseBytes = ByteArray(bufferSize ?: DEFAULT_BUFFER_SIZE)
                dataInputStream.close()
                it.onSuccess(responseBytes)
            } catch (e: Exception) {
                it.onError(e)
                println("Error occurred while receiving data: ${e.message}")
            } finally {
                if (socket?.isConnected!!)
                    beginReceiving() else socket.close()
            }
        }
    }

    private fun send(payload: ByteArray) {
        try {
            socket.getOutputStream().write(payload)
        } catch (e: Exception) {
            println("Error! Data cannot be sent: ${e.message}")
        }
    }
}