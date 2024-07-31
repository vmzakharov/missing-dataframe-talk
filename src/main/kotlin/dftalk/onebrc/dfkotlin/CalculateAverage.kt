package dftalk.onebrc.dfkotlin

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readCSV

import kotlin.time.TimeSource

const val MEASUREMENT_PATH = "onebrc"
const val MEASUREMENT_FILE = "measurements_10.txt"

fun main() {
    val timeSource = TimeSource.Monotonic

    val startTime = timeSource.markNow()

    val measurementFile = object {}::class.java.classLoader.getResource("$MEASUREMENT_PATH/$MEASUREMENT_FILE");

    val measurements = DataFrame.readCSV(
        measurementFile!!,
        header = listOf("Station", "Temperature"),
        delimiter = ';'
    );

    val loadTime = timeSource.markNow()

    println("Loaded ${loadTime - startTime} ms")

    val aggregated = measurements.groupBy("Station")
        .aggregate {
            min("Temperature") into "min"
            mean("Temperature") into "mean"
            max("Temperature") into "max"
        }
        .sortBy("Station")

    val aggregateTime = timeSource.markNow()

    println("Aggregated ${aggregateTime - loadTime} ms")

    aggregated.forEach {
        println("%s=%2.1f/%2.1f/%2.1f".format(it["Station"], it["min"], it["mean"], it["max"]))
    }
}