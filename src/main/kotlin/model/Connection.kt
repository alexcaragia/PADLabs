package model

import constants.ApplicationConstants
import java.net.Socket

class Connection(
    var byteArrayData: ByteArray = ByteArray(ApplicationConstants.BUFFER_SIZE),
    var socket: Socket? = null,
    var address: String = "",
    var title: String = ApplicationConstants.EMPTY_TITLE_PLACEHOLDER
)