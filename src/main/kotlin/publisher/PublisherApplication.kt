package publisher

import com.google.gson.Gson
import constants.ApplicationConstants
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import model.MessagePayload
import util.readLineWithHeader
import java.nio.charset.StandardCharsets

class PublisherApplication {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val publisherSocket =
                PublisherSocket(ApplicationConstants.BROKER_IP, ApplicationConstants.BROKER_PORT)
            val disposable = CompositeDisposable()
            disposable.add(
                publisherSocket.connect()
                    .subscribeOn(Schedulers.computation())
                    .subscribe({
                        if (it) {
                            while (true) {
                                val messagePayload = MessagePayload()
                                messagePayload.title = readLineWithHeader(ApplicationConstants.ENTER_TITLE)!!
                                messagePayload.message = readLineWithHeader(ApplicationConstants.ENTER_MESSAGE)!!
                                val payloadString = Gson().toJson(messagePayload)
                                val payloadBytes = payloadString.toByteArray(StandardCharsets.UTF_8)
                                publisherSocket.sendData(payloadBytes)
                            }
                        }
                    }, {
                        println("Error: ${it.message}")
                        disposable.dispose()
                    })
            )
        }
    }
}