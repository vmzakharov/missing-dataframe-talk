package dftalk.onebrc.dfec;

import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import io.github.vmzakharov.ecdataframe.dataset.CsvDataSet;
import io.github.vmzakharov.ecdataframe.dataset.CsvSchema;
import io.github.vmzakharov.ecdataframe.util.Stopwatch;
import org.eclipse.collections.api.factory.Lists;

import java.net.URI;
import java.nio.file.Path;

import static io.github.vmzakharov.ecdataframe.dataframe.AggregateFunction.avg2d;
import static io.github.vmzakharov.ecdataframe.dataframe.AggregateFunction.max;
import static io.github.vmzakharov.ecdataframe.dataframe.AggregateFunction.min;
import static io.github.vmzakharov.ecdataframe.dsl.value.ValueType.FLOAT;
import static io.github.vmzakharov.ecdataframe.dsl.value.ValueType.STRING;

public class CalculateAverage
{
    static private final String MEASUREMENT_PATH = "onebrc";
    static private final String MEASUREMENT_FILE = "measurements_10.txt";

    public static void main(String[] args) throws Exception
    {
        URI measurementFile = CalculateAverage.class.getClassLoader()
                                    .getResource(MEASUREMENT_PATH + "/" + MEASUREMENT_FILE)
                                    .toURI();

        CsvSchema msSchema = new CsvSchema()
                .addColumn("Station", STRING)
                .addColumn("Temperature", FLOAT)
                .separator(';')
                .hasHeaderLine(false);

        CsvDataSet msDataSet = new CsvDataSet(Path.of(measurementFile), "measurements", msSchema);

        System.out.println("Loading " + MEASUREMENT_FILE);
        Stopwatch sw = new Stopwatch();
        sw.start();
        DataFrame measurements = msDataSet.loadAsDataFrame();
        System.out.println(measurements.rowCount());
        sw.stop();

        System.out.printf("T: %,d, U: %,d, F: %,d\n", sw.totalMemoryBytes(), sw.freeMemoryBytes(), sw.usedMemoryBytes());
        System.out.printf("Time to load, ms: %,d\n", sw.elapsedTimeMillis());

        sw.start();
        DataFrame aggregated = measurements
                .aggregateBy(
                        Lists.immutable.of(min("Temperature", "Min"), avg2d("Temperature", "Mean"), max("Temperature", "Max")),
                        Lists.immutable.of("Station"))
                .sortBy(Lists.immutable.of("Station"));
        sw.stop();
        System.out.printf("T: %,d, U: %,d, F: %,d\n", sw.totalMemoryBytes(), sw.freeMemoryBytes(), sw.usedMemoryBytes());
        System.out.printf("Time to aggregate, ms: %,d\n", sw.elapsedTimeMillis());

        aggregated.forEach(row ->
                System.out.printf(
                        "%s=%2.1f/%2.1f/%2.1f\n",
                        row.getString("Station"), row.getFloat("Min"), row.getDouble("Mean"), row.getFloat("Max")));

//        System.out.println(aggregated.asCsvString());
    }
}
