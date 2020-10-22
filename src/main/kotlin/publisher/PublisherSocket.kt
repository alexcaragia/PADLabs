package publisher

import constants.ApplicationConstants
import io.reactivex.rxjava3.core.Single
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket

class PublisherSocket(private var address: String, private var port: Int) {
    private var serverSocket: ServerSocket = ServerSocket(port, ApplicationConstants.MAX_CONNECTIONS_LENGTH)
    private lateinit var clientSocket: Socket

    fun connect(): Single<Boolean> {
        return try {
            clientSocket = Socket(address, port)
            Single.create {
                it.onSuccess(if (clientSocket.isConnected) true.apply {
                    println("Sender connected")
                } else false.apply {
                    println("Sender not connected")
                })
            }
        } catch (e: Exception) {
            println("Error occurred when connecting to the socket: ${e.message}")
            Single.create {
                it.onError(e)
            }
        }
    }

    fun sendData(byteArray: ByteArray) {
        try {
            clientSocket = serverSocket.accept()
            val dataOutputStream = DataOutputStream(clientSocket.getOutputStream())
            dataOutputStream.write(byteArray)
            dataOutputStream.flush()
        } catch (e: Exception) {
            println("Data cannot be sent: ${e.message}")
        }
    }
}
