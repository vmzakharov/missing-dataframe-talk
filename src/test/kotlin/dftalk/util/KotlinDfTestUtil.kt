package dftalk.util

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import kotlin.test.assertEquals

public fun assertDfEquals(expected: DataFrame<Any?>, actual: DataFrame<Any?>) {
    assertEquals(expected.columnNames(), actual.columnNames(), "columns do not match")
    assertEquals(expected.schema(), actual.schema(), "schemas do not match")
    assertEquals(expected.rowsCount(), actual.rowsCount(), "row counts do not match")

    for (rowIndex in 0 until expected.rowsCount()) {
        for (columnName in expected.columnNames()) {
            assertEquals(
                expected[columnName][rowIndex],
                actual[columnName][rowIndex],
                "value mismatch at row $rowIndex column $columnName"
            )
        }
    }
}
