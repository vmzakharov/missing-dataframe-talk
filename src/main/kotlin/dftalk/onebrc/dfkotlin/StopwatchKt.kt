package dftalk.onebrc.dfkotlin

import kotlin.time.TimeSource

class StopwatchKt {
    private var startTimeMark = TimeSource.Monotonic.markNow();
    private var endTimeMark = TimeSource.Monotonic.markNow();

    var totalMemoryBytes: Long = 0
        private set
    var freeMemoryBytes: Long = 0
        private set
    val usedMemoryBytes
        get() = totalMemoryBytes - freeMemoryBytes


    val elapsedTimeMillis: Long
        get() {
            return (this.endTimeMark - this.startTimeMark).inWholeMilliseconds
        }

    fun start(): StopwatchKt {
        recordMemoryUsage()
        this.startTimeMark = TimeSource.Monotonic.markNow()
        return this
    }

    fun stop(): StopwatchKt {
        this.endTimeMark = TimeSource.Monotonic.markNow()
        this.recordMemoryUsage()
        return this
    }

    fun printStats(message: String): StopwatchKt {
        println("T: %,d, U: %,d, F: %,d".format(this.totalMemoryBytes, this.usedMemoryBytes, this.freeMemoryBytes))
        println("${message}, ms: ${this.elapsedTimeMillis}")
        return this
    }

    private fun recordMemoryUsage() {
        val runtime = Runtime.getRuntime()
        this.freeMemoryBytes = runtime.freeMemory()
        this.totalMemoryBytes = runtime.totalMemory()
    }
}