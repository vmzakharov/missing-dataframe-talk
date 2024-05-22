package dftalk.donuts.dfec;

import dftalk.util.dfec.DataFrameTestUtil;
import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import io.github.vmzakharov.ecdataframe.dataframe.DfColumnSortOrder;
import io.github.vmzakharov.ecdataframe.dataframe.util.DataFramePrettyPrint;
import io.github.vmzakharov.ecdataframe.util.ConfigureMessages;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.util.Set;

import static io.github.vmzakharov.ecdataframe.dataframe.AggregateFunction.sum;
import static io.github.vmzakharov.ecdataframe.dataframe.DfColumnSortOrder.ASC;
import static io.github.vmzakharov.ecdataframe.dataframe.DfColumnSortOrder.DESC;
import static org.junit.jupiter.api.Assertions.*;

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
                .addRow("Alice", TOMORROW, "Blueberry", 2)
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

        DataFrame aggregated =
                this.orders
                        .aggregateBy(
                                Lists.immutable.of(sum("Quantity")),
                                Lists.immutable.of("Donut"));

        System.out.println(aggregated);

        DataFrame sorted = aggregated.sortBy(
                Lists.immutable.of("Quantity", "Donut"),
                Lists.immutable.of(DESC, ASC));

        System.out.println(sorted.asCsvString());

        System.out.println(donutsInPopularityOrder.asCsvString());

        DataFrameTestUtil.assertEquals(
                new DataFrame("expected").
                        addStringColumn("Donut", Lists.immutable.of("Old Fashioned", "Apple Cider", "Jelly", "Blueberry", "Pumpkin Spice")),
                donutsInPopularityOrder);
    }

    @Test
    public void customersWithLargeDeliveriesTomorrow()
    {
        DataFrame tomorrowsOrders = this.orders
                .selectBy("DeliveryDate == toDate('" + TOMORROW +"') and Quantity >= 12");

        System.out.println(tomorrowsOrders);

        DataFrame justCustomers = tomorrowsOrders.distinct(Lists.immutable.of("Customer"));


        RichIterable<String> tomorrowsCustomers = tomorrowsOrders.getStringColumn("Customer")
                                                                 .toList();

        System.out.println(tomorrowsCustomers.makeString("\n"));

        assertEquals(Lists.immutable.of("Alice", "Bob", "Dave"), tomorrowsCustomers);
    }

    @Test
    public void totalSpendPerCustomer()
    {
        this.orders.lookupIn(this.menu)
                .match("Donut", "Donut")
                .select(Lists.immutable.of("Price", "DiscountPrice"))
                .resolveLookup();

        this.orders.addColumn("OrderPrice", "(Quantity < 12 ? Price : DiscountPrice) * Quantity");

        System.out.println(new DataFramePrettyPrint().prettyPrint(this.orders));

        DataFrame spendPerCustomer =
                this.orders
                    .aggregateBy(
                        Lists.immutable.of(sum("OrderPrice", "Total Spend")),
                        Lists.immutable.of("Customer"))
                    .sortBy(Lists.immutable.of("Customer"));

        System.out.println(new DataFramePrettyPrint().prettyPrint(spendPerCustomer));

        DataFrameTestUtil.assertEquals(
                new DataFrame("expected")
                        .addStringColumn("Customer").addDoubleColumn("Total Spend")
                        .addRow("Alice", 48.30)
                        .addRow("Bob",   11.55)
                        .addRow("Carol", 10.80)
                        .addRow("Dave",  10.80),
                spendPerCustomer,
                0.00001
        );
    }

    @Test
    public void donutCountPerCustomerPerDay()
    {
        DataFrame pivot = this.orders.pivot(
                Lists.immutable.of("Customer"),
                "DeliveryDate",
                Lists.immutable.of(sum("Quantity"))
        );

        System.out.println(new DataFramePrettyPrint().prettyPrint(pivot));
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
