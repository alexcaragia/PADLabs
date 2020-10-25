package publisher

import com.google.gson.Gson
import constants.ApplicationConstants
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.internal.schedulers.SingleScheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import model.MessagePayload
import util.readLineWithHeader
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class PublisherApplication {
    companion object {

        private val disposable = CompositeDisposable()

        @JvmStatic
        fun main(args: Array<String>) {
            val publisherSocket =
                PublisherSocket(ApplicationConstants.BROKER_IP, ApplicationConstants.BROKER_PORT)
            observeData(publisherSocket)
        }

        private fun observeData(publisherSocket: PublisherSocket) {
            disposable.add(
                publisherSocket.connect()
                    .subscribe({ isConnected ->
                        if (isConnected) {
                            while (isConnected) {
                                val messagePayload = MessagePayload()
                                writeData(messagePayload)
                                val payloadString = Gson().toJson(messagePayload)
                                val payloadBytes = payloadString.toByteArray(StandardCharsets.UTF_8)
                                sendData(publisherSocket, payloadBytes)
                            }
                        }
                    }, { println("Error: ${it.message}") })
            )
        }

        private fun writeData(messagePayload: MessagePayload) {
            messagePayload.title = readLineWithHeader(ApplicationConstants.ENTER_TITLE)
            messagePayload.message = readLineWithHeader(ApplicationConstants.ENTER_MESSAGE)
        }

        private fun sendData(publisherSocket: PublisherSocket, payloadBytes: ByteArray) {
            publisherSocket.sendData(payloadBytes)
                .subscribe({ complete ->
                    println(if (complete) "Operation complete" else "Operation Failed")
                }, {
                    println("Error occurred while sending data: ${it.message}")
                })
        }
    }
}