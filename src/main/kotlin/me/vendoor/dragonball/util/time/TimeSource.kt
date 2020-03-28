package me.vendoor.dragonball.util.time

import java.time.Clock

object TimeSource {
    private val clock = Clock.systemUTC()

    fun currentTimestamp() = clock.millis()
}