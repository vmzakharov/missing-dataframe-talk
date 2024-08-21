package dftalk.onebrc.tablesaw;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.net.URL;

import static tech.tablesaw.aggregate.AggregateFunctions.max;
import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.min;
import static tech.tablesaw.api.ColumnType.FLOAT;
import static tech.tablesaw.api.ColumnType.STRING;

public class CalculateAverage
{
    static private final String PATH = "onebrc";
    static private final String FILE = "measurements_10.txt";

    public static void main(String[] args) throws Exception
    {
        URL measurementFile = CalculateAverage.class.getClassLoader()
                                    .getResource(PATH + "/" + FILE);

        CsvReadOptions options = CsvReadOptions
                .builder(measurementFile)
                .columnTypes(new ColumnType[] {STRING, FLOAT})
                .separator(';')
                .header(false)
                .build();

        Table measurements = Table.read().usingOptions(options);
        measurements.column(0).setName("Station");
        measurements.column(1).setName("Temperature");

        Table aggregated = measurements
                .summarize("Temperature", min, mean, max).by("Station")
                .sortOn("Station");

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
