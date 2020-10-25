package broker

import constants.ApplicationConstants
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import model.Connection
import util.errorLog
import util.handleBytes
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.ObjectInputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.*

class BrokerSocket(private var address: String, private var port: Int) {
    private var serverSocket: ServerSocket = ServerSocket()

    fun start(): Single<Boolean> {
        return Single.create { emitter ->
            try {
                serverSocket.bind(InetSocketAddress(address, port), ApplicationConstants.MAX_CONNECTIONS_LENGTH)
                emitter.onSuccess(if (serverSocket.isBound) true else false.apply {
                    println("Broker not connected")
                })
            } catch (e: Exception) {
                println("Error occurred while binding: ${e.message}")
                emitter.onError(e)
            }
        }
    }

    fun accept(): Single<Connection> {
        return Single.create { emitter ->
            val connectionInfo = Connection()
            try {
                val socket: Socket = serverSocket.accept()
                connectionInfo.socket = socket
                connectionInfo.address = connectionInfo.socket?.inetAddress.toString()
                emitter.onSuccess(connectionInfo)
            } catch (e: Exception) {
                emitter.onError(e)
                println("Cannot accept connection: ${e.message}")
            }
        }
    }

    fun receiveData(connection: Connection): Single<ByteArray> {
        return Single.create {
            try {
                val objectInputStream = ObjectInputStream(connection.socket?.getInputStream())
                val messagePayload = objectInputStream.readObject() as String
                it.onSuccess(messagePayload.toByteArray())
                objectInputStream.close()
            } catch (e: Exception) {
                it.onError(e)
            } finally {
                /*receiveData(connection).subscribe({ bytePayload ->
                    it.onSuccess(bytePayload)
                }, { exception ->
                    println("Error occurred: ${exception.message}")
                    var address = connection.socket?.remoteSocketAddress.toString()
                    connection.socket?.close()
                    //TODO Remove from storage
                })*/
            }
        }
    }
}