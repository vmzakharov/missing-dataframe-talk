package dftalk.onebrc.dfkotlin

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readCSV

fun main() {
    val FILE = "onebrc/measurements_10.txt"

    val measurementFile = object {}::class.java.classLoader.getResource(FILE)

    val sw = StopwatchKt().start()

    val measurements = DataFrame.readCSV(
        measurementFile!!,
        header = listOf("Station", "Temperature"),
        delimiter = ';'
    );

    sw.stop().printStats("Time to load").start()

    val aggregated = measurements.groupBy("Station")
        .aggregate {
            min("Temperature") into "min"
            mean("Temperature") into "mean"
            max("Temperature") into "max"
        }
        .sortBy("Station")

    sw.stop().printStats("Time to aggregate")

    aggregated.forEach {
        println(
            "%s=%2.1f/%2.1f/%2.1f".format(it["Station"], it["min"], it["mean"], it["max"])
        )
    }
}
