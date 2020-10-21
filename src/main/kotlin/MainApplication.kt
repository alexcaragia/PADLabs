import com.google.gson.Gson
import constants.ApplicationConstants
import io.reactivex.rxjava3.internal.schedulers.IoScheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import model.MessagePayload
import publisher.PublisherSocket
import util.readLineWithMessageHeader
import java.nio.charset.StandardCharsets

class MainApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val publisherSocket = PublisherSocket(ApplicationConstants.BROKER_IP, ApplicationConstants.BROKER_PORT)
            publisherSocket.connect()
                .subscribeOn(Schedulers.io())
                .observeOn(IoScheduler())
                .subscribe { isConnected ->
                    if (isConnected) {
                        val title =
                            "Enter title".readLineWithMessageHeader() ?: ApplicationConstants.EMPTY_TITLE_PLACEHOLDER
                        val message =
                            "Enter message".readLineWithMessageHeader()
                                ?: ApplicationConstants.EMPTY_MESSAGE_PLACEHOLDER
                        val messagePayload = MessagePayload(title, message)
                        val jsonString = Gson().toJson(messagePayload)
                        publisherSocket.sendData(jsonString.toByteArray(StandardCharsets.UTF_8))
                    }
                }
        }
    }
}