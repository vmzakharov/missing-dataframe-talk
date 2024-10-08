package dftalk.onebrc.dfkotlin

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readCSV

fun main() {
    val FILE = "onebrc/measurements_10.txt"

    val measurementFile = object {}::class.java.classLoader.getResource(FILE)

    val measurements = DataFrame.readCSV(
        measurementFile!!,
        header = listOf("Station", "Temperature"),
        delimiter = ';'
    );

    val aggregated = measurements.groupBy("Station")
        .aggregate {
            min("Temperature") into "min"
            mean("Temperature") into "mean"
            max("Temperature") into "max"
        }
        .sortBy("Station")

    aggregated.forEach {
        println(
            "%s=%2.1f/%2.1f/%2.1f".format(it["Station"], it["min"], it["mean"], it["max"])
        )
    }
}
