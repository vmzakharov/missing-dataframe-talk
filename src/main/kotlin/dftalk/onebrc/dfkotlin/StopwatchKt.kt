package dftalk.onebrc.dfkotlin

import kotlin.time.TimeMark
import kotlin.time.TimeSource

class StopwatchKt
{
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

    fun start()
    {
        recordMemoryUsage()
        this.startTimeMark = TimeSource.Monotonic.markNow()
    }

    fun stop()
    {
        this.endTimeMark = TimeSource.Monotonic.markNow()
        this.recordMemoryUsage()
    }

    fun recordMemoryUsage()
    {
        val runtime = Runtime.getRuntime()
        this.freeMemoryBytes = runtime.freeMemory()
        this.totalMemoryBytes = runtime.totalMemory()
    }
}