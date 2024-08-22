package dftalk.onebrc.tablesaw;

import dftalk.onebrc.util.Stopwatch;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.net.URL;

import static tech.tablesaw.aggregate.AggregateFunctions.max;
import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.min;
import static tech.tablesaw.api.ColumnType.FLOAT;
import static tech.tablesaw.api.ColumnType.STRING;

public class CalculateAverageWithLogging
{
    static private final String FILE = "onebrc/measurements_10.txt";

    public static void main(String[] args) throws Exception
    {
        URL measurementFile = CalculateAverageWithLogging.class.getClassLoader()
                                                               .getResource(FILE);

        CsvReadOptions options = CsvReadOptions
                .builder(measurementFile)
                .columnTypes(new ColumnType[] {STRING, FLOAT})
                .separator(';')
                .header(false)
                .build();

        System.out.println("Loading " + FILE);

        Stopwatch sw = new Stopwatch().start();

        Table measurements = Table.read().usingOptions(options);
        measurements.column(0).setName("Station");
        measurements.column(1).setName("Temperature");

        System.out.println(measurements.rowCount());

        sw.stop().printStats("Time to load").start();

        Table aggregated = measurements
                .summarize("Temperature", min, mean, max).by("Station")
                .sortOn("Station");

        sw.stop().printStats("Time to aggregate");

        aggregated.forEach(row ->
                System.out.printf(
                        "%s=%2.1f/%2.1f/%2.1f\n",
                        row.getString("Station"),
                        row.getDouble("Min [Temperature]"),
                        row.getDouble("Mean [Temperature]"),
                        row.getDouble("Max [Temperature]"))
        );
    }
}
