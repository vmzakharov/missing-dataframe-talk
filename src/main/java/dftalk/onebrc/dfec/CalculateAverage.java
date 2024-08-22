package dftalk.onebrc.dfec;

import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import io.github.vmzakharov.ecdataframe.dataset.CsvDataSet;
import io.github.vmzakharov.ecdataframe.dataset.CsvSchema;
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
    static private final String FILE = "onebrc/measurements_10.txt";

    public static void main(String[] args) throws Exception
    {
        URI measurementFile = CalculateAverage.class.getClassLoader()
                                    .getResource(FILE)
                                    .toURI();

        CsvSchema msSchema = new CsvSchema()
                .addColumn("Station", STRING)
                .addColumn("Temperature", FLOAT)
                .separator(';')
                .hasHeaderLine(false);

        CsvDataSet msDataSet = new CsvDataSet(Path.of(measurementFile), "measurements", msSchema);

        DataFrame measurements = msDataSet.loadAsDataFrame();

        DataFrame aggregated = measurements
                .aggregateBy(
                        Lists.immutable.of(min("Temperature", "Min"), avg2d("Temperature", "Mean"), max("Temperature", "Max")),
                        Lists.immutable.of("Station"))
                .sortBy(Lists.immutable.of("Station"));

        aggregated.forEach(row ->
                System.out.printf(
                        "%s=%2.1f/%2.1f/%2.1f\n",
                        row.getString("Station"),
                        row.getFloat("Min"),
                        row.getDouble("Mean"),
                        row.getFloat("Max")));
    }
}
