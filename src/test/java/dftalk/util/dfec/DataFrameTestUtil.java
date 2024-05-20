package dftalk.util.dfec;

import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import io.github.vmzakharov.ecdataframe.dataframe.util.DataFrameCompare;
import org.junit.jupiter.api.Assertions;

final public class DataFrameTestUtil
{
    private DataFrameTestUtil()
    {
        // Utility class
    }

    static public void assertEquals(DataFrame expected, DataFrame actual)
    {
        DataFrameCompare comparer = new DataFrameCompare();

        if (!comparer.equal(expected, actual))
        {
            Assertions.fail(comparer.reason());
        }
    }

    static public void assertEquals(DataFrame expected, DataFrame actual, double tolerance)
    {
        DataFrameCompare comparer = new DataFrameCompare();

        if (!comparer.equal(expected, actual, tolerance))
        {
            Assertions.fail(comparer.reason());
        }
    }
}
