package storage

import model.MessagePayload
import java.util.concurrent.ConcurrentLinkedQueue

object MessagePayloadStorage {

    private val payloadQueue: ConcurrentLinkedQueue<MessagePayload> = ConcurrentLinkedQueue()
    val isEmpty = payloadQueue.isEmpty()

    fun add(messagePayload: MessagePayload) {
        payloadQueue.add(messagePayload)
    }

    fun getNext(): MessagePayload? {
        val messagePayload: MessagePayload? = null
        return try {
            payloadQueue.remove(messagePayload)
            messagePayload
        } catch (e: Exception) {
            println("Error : ${e.message}")
            messagePayload
        }
    }
}