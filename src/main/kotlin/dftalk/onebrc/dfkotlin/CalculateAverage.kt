package dftalk.onebrc.dfkotlin

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readCSV
import java.nio.file.Path
import kotlin.time.TimeSource

const val PATH = "onebrc"
const val FILE = "measurements_10.txt"

fun main() {
    val startMark = TimeSource.Monotonic.markNow()

    val sw = StopwatchKt()

    var measurementFile = object {}::class.java.classLoader.getResource("$PATH/$FILE")

    measurementFile = Path.of("D:\\projects\\1brc\\measurements_100MM.txt").toUri()
        .toURL()

    sw.start()

    val measurements = DataFrame.readCSV(
        measurementFile!!,
        header = listOf("Station", "Temperature"),
        delimiter = ';'
    );

    sw.stop()

    println("T: %,d, U: %,d, F: %,d".format(sw.totalMemoryBytes, sw.freeMemoryBytes, sw.usedMemoryBytes))
    println("Time to load, ms: ${sw.elapsedTimeMillis}")

    sw.start()

    val aggregated = measurements.groupBy("Station")
        .aggregate {
            min("Temperature") into "min"
            mean("Temperature") into "mean"
            max("Temperature") into "max"
        }
        .sortBy("Station")

    sw.stop()

    println("T: %,d, U: %,d, F: %,d".format(sw.totalMemoryBytes, sw.freeMemoryBytes, sw.usedMemoryBytes))
    println("Time to aggregate. ms: ${sw.elapsedTimeMillis}")

    val endMark = TimeSource.Monotonic.markNow();
    println("Total Time, ms ${(endMark - startMark).inWholeMilliseconds}")

    aggregated.forEach {
        println(
            "%s=%2.1f/%2.1f/%2.1f".format(it["Station"], it["min"], it["mean"], it["max"])
        )
    }
}