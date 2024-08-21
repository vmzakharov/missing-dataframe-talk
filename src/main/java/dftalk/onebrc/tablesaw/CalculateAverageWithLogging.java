package dftalk.onebrc.tablesaw;

import io.github.vmzakharov.ecdataframe.util.Stopwatch;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.net.URL;
import java.nio.file.Path;

import static tech.tablesaw.aggregate.AggregateFunctions.*;
import static tech.tablesaw.api.ColumnType.FLOAT;
import static tech.tablesaw.api.ColumnType.STRING;

public class CalculateAverageWithLogging
{
    static private final String PATH = "onebrc";
    static private final String FILE = "measurements_10.txt";

    public static void main(String[] args) throws Exception
    {
        URL measurementFile = CalculateAverageWithLogging.class.getClassLoader()
                                                               .getResource(PATH + "/" + FILE);

        measurementFile = Path.of("D:\\projects\\1brc\\measurements_100MM.txt").toUri()
                              .toURL();

        CsvReadOptions options = CsvReadOptions
                .builder(measurementFile)
                .columnTypes(new ColumnType[] {STRING, FLOAT})
                .separator(';')
                .header(false)
                .build();

        System.out.println("Loading " + FILE);
        Stopwatch sw = new Stopwatch();
        sw.start();

        Table measurements = Table.read().usingOptions(options);
        measurements.column(0).setName("Station");
        measurements.column(1).setName("Temperature");

        System.out.println(measurements.rowCount());
        sw.stop();

        System.out.printf("T: %,d, U: %,d, F: %,d\n", sw.totalMemoryBytes(), sw.freeMemoryBytes(), sw.usedMemoryBytes());
        System.out.printf("Time to load, ms: %,d\n", sw.elapsedTimeMillis());

        sw.start();
        Table aggregated = measurements
                .summarize("Temperature", min, mean, max).by("Station")
                .sortOn("Station");
        sw.stop();

        System.out.printf("T: %,d, U: %,d, F: %,d\n", sw.totalMemoryBytes(), sw.freeMemoryBytes(), sw.usedMemoryBytes());
        System.out.printf("Time to aggregate, ms: %,d\n", sw.elapsedTimeMillis());

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
