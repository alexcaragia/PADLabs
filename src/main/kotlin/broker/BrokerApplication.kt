package broker

import constants.ApplicationConstants
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import util.errorLog
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class BrokerApplication {

    companion object {

        private val disposable= CompositeDisposable()

        @JvmStatic
        fun main(args: Array<String>) {
            try {
                val brokerSocket = BrokerSocket(ApplicationConstants.BROKER_IP, ApplicationConstants.BROKER_PORT)
                disposable.add(
                    brokerSocket.start()
                        .subscribe({ isConnected ->
                            println("Broker socket connected : $isConnected")
                            if (isConnected) brokerSocket.accept().subscribe({ connection ->
                                if (connection.title.isNotEmpty())
                                    brokerSocket.receiveData(connection).subscribe({
                                        println("Message ${String(it)}")
                                    }, { it.errorLog() })
                            }, { it.errorLog() })
                        }, {
                            println("Error occurred while connecting Broker: ${it.message}")
                        })
                )
                readLine()
            } catch (e: Exception) {
                println("Error: $e")
            }
        }
    }
}