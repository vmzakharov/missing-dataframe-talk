package dftalk.donuts.dfec;

import dftalk.util.dfec.DataFrameTestUtil;
import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import io.github.vmzakharov.ecdataframe.dataframe.util.DataFramePrettyPrint;
import io.github.vmzakharov.ecdataframe.util.ConfigureMessages;
import org.eclipse.collections.api.factory.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.github.vmzakharov.ecdataframe.dataframe.AggregateFunction.sum;
import static io.github.vmzakharov.ecdataframe.dataframe.DfColumnSortOrder.ASC;
import static io.github.vmzakharov.ecdataframe.dataframe.DfColumnSortOrder.DESC;

public class DonutDataFrameEcTest
{
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate YESTERDAY = TODAY.minusDays(1);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);

    private DataFrame menu;
    private DataFrame customers;
    private DataFrame orders;

    @BeforeAll
    public static void initializeErrorMessages()
    {
        ConfigureMessages.initialize();
    }

    @BeforeEach
    public void configureDonutShop()
    {
        this.customers = new DataFrame("customers")
                .addStringColumn("Name").addStringColumn("Street").addStringColumn("City").addStringColumn("State")
                .addRow("Alice", "902 S Pacific St", "Las Vegas", "NM")
                .addRow("Bob", "405 Main St", "Dallas", "SD")
                .addRow("Carol", "12300 State St", "Atlanta", "MI")
                .addRow("Dave", "102 S Main St", "Phoenix", "OR")
                ;

        this.menu = new DataFrame("menu")
                .addStringColumn("Donut").addDoubleColumn("Price").addDoubleColumn("DiscountPrice")
                .addRow("Blueberry", 1.25, 1.00)
                .addRow("Old Fashioned", 1.00, 0.90)
                .addRow("Pumpkin Spice", 0.75, 0.65)
                .addRow("Jelly", 1.50, 1.25)
                .addRow("Apple Cider", 1.50, 1.25)
                ;
        
        this.orders = new DataFrame("orders")
                .addStringColumn("Customer").addDateColumn("DeliveryDate").addStringColumn("Donut").addLongColumn("Quantity")
                .addRow("Alice", YESTERDAY, "Old Fashioned", 12)
                .addRow("Alice", YESTERDAY, "Blueberry", 2)
                .addRow("Bob",   YESTERDAY, "Old Fashioned", 12)
                .addRow("Alice", TODAY, "Apple Cider", 12)
                .addRow("Alice", TODAY, "Blueberry", 2)
                .addRow("Carol", TODAY, "Old Fashioned", 12)
                .addRow("Dave",  TOMORROW, "Old Fashioned", 12)
                .addRow("Alice", TOMORROW, "Jelly", 12)
                .addRow("Carol", TOMORROW, "Blueberry", 2)
                .addRow("Bob",   TOMORROW, "Pumpkin Spice", 1)
                ;
    }

    @Test
    public void donutsInPopularityOrder()
    {
        DataFrame donutsInPopularityOrder =
                this.orders
                        .aggregateBy(
                                Lists.immutable.of(sum("Quantity")),
                                Lists.immutable.of("Donut"))
                        .sortBy(Lists.immutable.of("Quantity", "Donut"),
                                Lists.immutable.of(DESC, ASC))
                        .keepColumns(Lists.immutable.of("Donut"));

        DataFrameTestUtil.assertEquals(
                new DataFrame("expected").
                        addStringColumn("Donut", Lists.immutable.of("Old Fashioned", "Apple Cider", "Jelly", "Blueberry", "Pumpkin Spice")),
                donutsInPopularityOrder);
    }

    @Test
    public void priorityOrdersTomorrow()
    {
        DataFrame priorityOrdersTomorrow = this.orders
                .selectBy(
                        "DeliveryDate == toDate('%s') and (Quantity >= 12 or Customer == 'Bob')"
                        .formatted(TOMORROW)
                );

//        System.out.println(priorityOrdersTomorrow);

        DataFrameTestUtil.assertEquals(
                new DataFrame("expected")
                        .addStringColumn("Customer").addDateColumn("DeliveryDate").addStringColumn("Donut").addLongColumn("Quantity")
                        .addRow("Dave",  TOMORROW, "Old Fashioned", 12)
                        .addRow("Alice", TOMORROW, "Jelly", 12)
                        .addRow("Bob",   TOMORROW, "Pumpkin Spice", 1),
                priorityOrdersTomorrow
        );
    }

    @Test
    public void totalSpendPerCustomer()
    {
        this.orders.lookupIn(this.menu)
                .match("Donut", "Donut")
                .select(Lists.immutable.of("Price", "DiscountPrice"))
                .resolveLookup();

        this.orders.addColumn("OrderPrice", "(Quantity < 12 ? Price : DiscountPrice) * Quantity");

//        System.out.println(new DataFramePrettyPrint().prettyPrint(this.orders));

        DataFrame spendPerCustomer =
                this.orders
                    .aggregateBy(
                        Lists.immutable.of(sum("OrderPrice", "Total Spend")),
                        Lists.immutable.of("Customer"))
                    .sortBy(Lists.immutable.of("Customer"));

//        System.out.println(new DataFramePrettyPrint().prettyPrint(spendPerCustomer));

        DataFrameTestUtil.assertEquals(
                new DataFrame("expected")
                        .addStringColumn("Customer").addDoubleColumn("Total Spend")
                        .addRow("Alice", 45.80)
                        .addRow("Bob",   11.55)
                        .addRow("Carol", 13.30)
                        .addRow("Dave",  10.80),
                spendPerCustomer,
                0.00001
        );
    }

    @Test
    public void donutCountPerCustomerPerDay()
    {
        DataFrame donutsPerCustomerPerDay = this.orders.pivot(
                Lists.immutable.of("Customer"),
                "DeliveryDate",
                Lists.immutable.of(sum("Quantity"))
        );

        System.out.println(new DataFramePrettyPrint().prettyPrint(donutsPerCustomerPerDay));

        DataFrameTestUtil.assertEquals(
                new DataFrame("expected")
                        .addStringColumn("Customer")
                        .addLongColumn(YESTERDAY.toString())
                        .addLongColumn(TODAY.toString())
                        .addLongColumn(TOMORROW.toString())
                        .addRow("Alice", 14, 14, 12)
                        .addRow("Bob"  , 12,  0,  1)
                        .addRow("Carol",  0, 12,  2)
                        .addRow("Dave" ,  0,  0, 12),
                donutsPerCustomerPerDay
        );
    }

    @Disabled
    @Test
    public void printEm()
    {
        System.out.println(this.customers);
        System.out.println(this.menu);
        System.out.println(this.orders);
    }
}
