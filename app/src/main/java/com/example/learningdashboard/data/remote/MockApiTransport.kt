package com.example.learningdashboard.data.remote

import java.io.IOException
import kotlinx.coroutines.delay

class OfflineException : IOException("No internet connection")
class MockServerException : IOException("Simulated server failure")

class MockApiTransport(
    private val connectivity: ConnectivityChecker,
    private val latencyMillis: Long = 700L
) {
    init {
        require(latencyMillis >= 0)
    }

    suspend fun <T> execute(block: suspend () -> T): T {
        if (!connectivity.isOnline()) throw OfflineException()
        delay(latencyMillis)
        // Also simulate a connection lost while a request was in flight.
        if (!connectivity.isOnline()) throw OfflineException()
        return block()
    }
}
