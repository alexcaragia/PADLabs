package util

import com.google.gson.Gson
import constants.ApplicationConstants
import model.Connection
import model.MessagePayload
import storage.MessagePayloadStorage
import java.nio.charset.StandardCharsets

fun readLineWithHeader(header: String): String {
    println(header)
    return readLine()?.toLowerCase()!!
}

fun ByteArray.handleBytes(connection: Connection): String {
    val responseString = String(this, StandardCharsets.UTF_8)
    if (responseString.startsWith(ApplicationConstants.SUBSCRIBE)) {
        connection.title = responseString.split(ApplicationConstants.SUBSCRIBE, ignoreCase = true).last()
    } else {
        val messagePayload = Gson().fromJson(responseString, MessagePayload::class.java)
        MessagePayloadStorage.add(messagePayload)
    }
    return responseString
}

fun Throwable.errorLog() {
    println("Error occurred : ${this.message}")
}

fun ByteArray.handleBytes(): MessagePayload {
    val payloadString = String(this, StandardCharsets.UTF_8)
    val payload = Gson().fromJson(payloadString, MessagePayload::class.java)
    println(payload.message)
    return payload
}