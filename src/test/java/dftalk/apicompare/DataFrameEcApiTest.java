package dftalk.apicompare;

import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.primitive.ImmutableDoubleList;
import org.eclipse.collections.api.partition.list.PartitionImmutableList;
import org.eclipse.collections.api.tuple.Twin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

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
    {}

    @Test
    public void reject()
    {}

    @Test
    public void sort()
    {}

    @Test
    public void inject()
    {
        String customerLine = this.donutOrders.getStringColumn("Customer").injectIntoBreakOnNulls(
            "Head", (aggregate, current) -> aggregate + "->" + current
        );

        assertEquals("Head->Alice->Alice->Bob->Alice->Alice->Carol->Dave->Alice->Carol->Bob", customerLine);
    }
}
