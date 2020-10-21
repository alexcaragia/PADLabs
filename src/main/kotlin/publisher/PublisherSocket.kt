package publisher

import io.reactivex.rxjava3.core.Single
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class PublisherSocket(address: String, private var port: Int) {
    private var inetAddress: InetAddress = InetAddress.getByName(address)
    private val socket: Socket by lazy { Socket(inetAddress, port) }

    fun connect(): Single<Boolean> = Single.create {
        try {
            socket.connect(InetSocketAddress(inetAddress, port))
            it.onSuccess(socket.isConnected)
        } catch (exception: Exception) {
            it.onError(exception)
            println("Error, sender not connected: $exception")
        }
    }

    fun sendData(byteArray: ByteArray){
        try {
            val dataOutputStream = DataOutputStream(socket.getOutputStream())
            dataOutputStream.write(byteArray)
            dataOutputStream.flush()
        } catch (e: Exception) {
            println("Data cannot be sent: ${e.message}")
        }
    }
}