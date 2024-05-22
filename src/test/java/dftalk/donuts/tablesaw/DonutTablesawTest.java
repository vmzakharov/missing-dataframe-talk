package dftalk.donuts.tablesaw;

import dftalk.util.tablesaw.TablesawTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tech.tablesaw.aggregate.AggregateFunctions.sum;

public class DonutTablesawTest
{
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate YESTERDAY = TODAY.minusDays(1);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);

    private Table menu;
    private Table customers;
    private Table orders;

    @BeforeEach
    public void configureDonutShop()
    {
        this.menu =
                Table.create("menu")
                     .addColumns(
                             StringColumn.create("Donut",
                                     new String[]{"Blueberry", "Old Fashioned", "Pumpkin Spice", "Jelly", "Apple Cider"}),
                             DoubleColumn.create("Price",
                                     new double[]{1.25, 1.00, 0.75, 1.50, 1.50}),
                             DoubleColumn.create("DiscountPrice",
                                     new double[]{1.00, 0.90, 0.65, 1.25, 1.25})
                     );

        this.customers =
                Table.create("customers")
                     .addColumns(
                             StringColumn.create("Name", new String[]{"Alice", "Bob", "Carol", "Dave"}),
                             StringColumn.create(
                                     "Street",
                                     new String[]{"902 S Pacific St", "405 Main St", "12300 State St", "102 S Main St"}),
                             StringColumn.create("City", new String[]{"Las Vegas", "Dallas", "Atlanta", "Phoenix"}),
                             StringColumn.create("State", new String[]{"NM", "SD", "MI", "OR"})
                     );

        this.orders =
                Table.create("orders")
                     .addColumns(
                             StringColumn.create(
                                     "Customer",
                                     "Alice", "Alice", "Bob", "Alice", "Alice", "Carol", "Dave", "Alice", "Alice", "Bob"),
                             DateColumn.create(
                                     "DeliveryDate",
                                     YESTERDAY, YESTERDAY, YESTERDAY, TODAY, TODAY,
                                     TODAY, TOMORROW, TOMORROW, TOMORROW, TOMORROW),
                             StringColumn.create(
                                     "Donut",
                                     "Old Fashioned", "Blueberry", "Old Fashioned", "Apple Cider", "Blueberry",
                                     "Old Fashioned", "Old Fashioned", "Jelly", "Blueberry", "Pumpkin Spice"
                             ),
                             LongColumn.create("Quantity", 12, 2, 12, 12, 2, 12, 12, 12, 2, 1)
                     );
    }

    @Disabled
    @Test
    public void printEm()
    {
        System.out.println(this.menu);
        System.out.println(this.customers);
        System.out.println(this.orders);
    }

    @Test
    public void donutsInPopularityOrder()
    {
        Table donutsInPopularityOrder = this.orders
                .summarize("Quantity", sum)
                .by("Donut")
                .sortOn("-Sum [Quantity]", "Donut")
                .retainColumns("Donut");

            TablesawTestUtil.assertEquals(
                Table.create("orders summary")
                    .addColumns(
                        StringColumn.create(
                                "Donut",
                                "Old Fashioned", "Apple Cider", "Jelly", "Blueberry", "Pumpkin Spice")
                    ),
                donutsInPopularityOrder
        );
    }

    @Test
    public void customersWithLargeDeliveriesTomorrow()
    {
        Table tomorrowsLargeOrders = this.orders.where(
              this.orders.dateColumn("DeliveryDate").isEqualTo(TOMORROW)
        );

        System.out.println(tomorrowsLargeOrders);

        List<String> tomorrowsCustomers = tomorrowsLargeOrders.stringColumn("Customer")
                                                         .asList();

        System.out.println(tomorrowsCustomers);
    }

    @Test
    public void totalSpendPerCustomer()
    {
    }

    @Test
    public void donutCountPerCustomerPerDay()
    {
    }
}
