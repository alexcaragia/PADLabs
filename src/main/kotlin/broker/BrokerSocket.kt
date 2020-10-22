package broker

import io.reactivex.rxjava3.core.Single
import model.Connection
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class BrokerSocket {
    private val serverSocket: ServerSocket by lazy { ServerSocket() }

    fun start(ip: String, port: Int) {
        serverSocket.bind(InetSocketAddress(InetAddress.getByName(ip), port))
        accept()
    }

    private fun accept(): Single<Connection> {
        return Single.create {
            val connectionInfo = Connection()
            try {
                val socket = Socket(serverSocket.inetAddress, serverSocket.localPort)
                connectionInfo.socket = socket
                connectionInfo.address = connectionInfo.socket?.remoteSocketAddress.toString()
            } catch (e: Exception) {
                println("Cannot accept connection: ${e.message}")
            } finally {
                accept()
            }
        }
    }
}