package publisher

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.io.DataOutputStream
import java.net.Socket

class PublisherSocket(private var address: String, private var port: Int) {
    private lateinit var clientSocket: Socket

    fun connect(): Observable<Boolean> {
        return Observable.create {
            try {
                clientSocket = Socket(address, port)
                println("""
                    Publisher socket connected: ${clientSocket.isConnected}
                    Running on
                    Address : ${clientSocket.inetAddress}
                    Port: ${clientSocket.port}
                """.trimIndent())
                it.onNext(clientSocket.isConnected)
                it.onComplete()
            } catch (e: Exception) {
                it.onError(e)
                println("Error occurred when connecting Publisher: ${e.message}")
            }
        }
    }

    fun sendData(byteArray: ByteArray): Single<Boolean> {
        return Single.create {
            println("Publisher sending data : ${String(byteArray)}")
            try {
                val dataOutputStream = DataOutputStream(clientSocket.getOutputStream())
                dataOutputStream.write(byteArray.size)
                dataOutputStream.write(byteArray)
                it.onSuccess(dataOutputStream.size() > 0)
                dataOutputStream.close()
            } catch (e: Exception) {
                println("Data cannot be sent: ${e.message}")
                it.onError(e)
            }
        }
    }
}
