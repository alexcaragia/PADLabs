package model

import java.io.Serializable

class MessagePayload(
    var title: String = "Empty title",
    var message: String = "Empty message"
) : Serializable