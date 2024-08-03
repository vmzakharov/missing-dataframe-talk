package dftalk.apicompare;

import dftalk.util.dfec.DataFrameTestUtil;
import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Twin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataFrameEcApiTest
{
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate YESTERDAY = TODAY.minusDays(1);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);

    private DataFrame donutOrders;

    @BeforeEach
    public void setupDonutOrders()
    {
        this.donutOrders = new DataFrame("DonutOrders")
                .addStringColumn("Customer").addDateColumn("DeliveryDate").addStringColumn("Donut")
                .addLongColumn("Quantity").addDoubleColumn("OrderPrice")
                .addRow("Alice", YESTERDAY, "Old Fashioned", 12, 10.80)
                .addRow("Alice", YESTERDAY, "Blueberry",      2,  2.50)
                .addRow("Bob",   YESTERDAY, "Old Fashioned", 12, 10.80)
                .addRow("Alice", TODAY,     "Apple Cider",   12, 15.00)
                .addRow("Alice", TODAY,     "Blueberry",      2,  2.50)
                .addRow("Carol", TODAY,     "Old Fashioned", 12, 10.80)
                .addRow("Dave",  TOMORROW,  "Old Fashioned", 12, 10.80)
                .addRow("Alice", TOMORROW,  "Jelly",         12, 15.00)
                .addRow("Carol", TOMORROW,  "Blueberry",      2,  2.50)
                .addRow("Bob",   TOMORROW,  "Pumpkin Spice",  1,  0.75)
        ;
    }

    @Test
    public void partition()
    {
        Twin<DataFrame> smallAndBigOrders = this.donutOrders.partition("Quantity < 12");

        assertEquals(4, smallAndBigOrders.getOne().rowCount());
        assertEquals(6, smallAndBigOrders.getTwo().rowCount());
    }

    @Test
    public void distinct()
    {
        DataFrame x = this.donutOrders.distinct(Lists.immutable.of("Customer", "DeliveryDate"));
        assertEquals(8, x.rowCount());
    }

    @Test
    public void select()
    {
        DataFrameTestUtil.assertEquals(
                new DataFrame("expected")
                        .addStringColumn("Customer").addDateColumn("DeliveryDate").addStringColumn("Donut")
                        .addLongColumn("Quantity").addDoubleColumn("OrderPrice")
                        .addRow("Carol", TODAY,     "Old Fashioned", 12, 10.80)
                        .addRow("Carol", TOMORROW,  "Blueberry",      2,  2.50)
                ,
                this.donutOrders.selectBy("Customer == 'Carol'")
        );
    }

    @Test
    public void reject()
    {
        DataFrameTestUtil.assertEquals(
                new DataFrame("expected")
                        .addStringColumn("Customer").addDateColumn("DeliveryDate").addStringColumn("Donut")
                        .addLongColumn("Quantity").addDoubleColumn("OrderPrice")
                        .addRow("Bob",   YESTERDAY, "Old Fashioned", 12, 10.80)
                        .addRow("Carol", TODAY,     "Old Fashioned", 12, 10.80)
                        .addRow("Dave",  TOMORROW,  "Old Fashioned", 12, 10.80)
                        .addRow("Carol", TOMORROW,  "Blueberry",      2,  2.50)
                        .addRow("Bob",   TOMORROW,  "Pumpkin Spice",  1,  0.75)
                ,
                this.donutOrders.selectBy("Customer != 'Alice'")
        );

    }

    @Test
    public void sort()
    {
        MutableList<String> blip = Lists.mutable.empty();

        this.donutOrders
                .sortBy(Lists.immutable.of("Customer", "OrderPrice"))
                .forEach(row -> blip.add(row.getString("Customer") + ":" + row.getDouble("OrderPrice")));

        assertEquals(
                "Alice:2.5;Alice:2.5;Alice:10.8;Alice:15.0;Alice:15.0;Bob:0.75;Bob:10.8;Carol:2.5;Carol:10.8;Dave:10.8",
                blip.makeString(";"));
    }

    @Test
    public void inject()
    {
        String customerLine = this.donutOrders.getStringColumn("Customer").injectIntoBreakOnNulls(
            "Head", (aggregate, current) -> aggregate + "->" + current
        );

        assertEquals("Head->Alice->Alice->Bob->Alice->Alice->Carol->Dave->Alice->Carol->Bob", customerLine);
    }
}
