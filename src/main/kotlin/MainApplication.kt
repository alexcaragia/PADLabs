import broker.BrokerSocket
import constants.ApplicationConstants

class MainApplication {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            try {
                val brokerSocket = BrokerSocket()
                brokerSocket.start(ApplicationConstants.BROKER_IP, ApplicationConstants.BROKER_PORT)
            } catch (e: Exception) {
                println("Error: $e")
            }
        }
    }
}