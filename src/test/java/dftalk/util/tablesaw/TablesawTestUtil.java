package dftalk.util.tablesaw;

import org.junit.jupiter.api.Assertions;
import tech.tablesaw.api.Table;

public class TablesawTestUtil
{
    private TablesawTestUtil()
    {
        // utility class
    }

    static public void assertEquals(Table expected, Table actual)
    {
        Assertions.assertEquals(expected.columnNames(), actual.columnNames(), "columns do not match");
        Assertions.assertEquals(expected.rowCount(), actual.rowCount(), "row counts do not match");

        int rowCount = expected.rowCount();
        int columnCount = expected.columnCount();

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++)
        {
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++)
            {
                Assertions.assertEquals(
                        expected.get(rowIndex, columnIndex),
                        actual.get(rowIndex, columnIndex),
                        "different values in row: " + rowIndex + ", col: " + columnIndex
                );
            }
        }
    }
}
