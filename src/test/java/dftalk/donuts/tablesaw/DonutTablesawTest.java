package dftalk.donuts.tablesaw;

import dftalk.util.tablesaw.TablesawTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.*;

import java.time.LocalDate;
import java.util.List;

import static tech.tablesaw.aggregate.AggregateFunctions.sum;
import static tech.tablesaw.api.QuerySupport.and;
import static tech.tablesaw.api.QuerySupport.or;

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
                             StringColumn.create(
                                     "Name",
                                     "Alice", "Bob", "Carol", "Dave"),
                             StringColumn.create(
                                     "Street",
                                     "902 S Pacific St", "405 Main St", "12300 State St", "102 S Main St"),
                             StringColumn.create(
                                     "City",
                                     "Las Vegas", "Dallas", "Atlanta", "Phoenix"),
                             StringColumn.create(
                                     "State",
                                     "NM", "SD", "MI", "OR")
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
    public void priorityOrdersTomorrow()
    {
        Table priorityOrdersTomorrow = this.orders.where(
                and(
                        t -> t.dateColumn("DeliveryDate").isEqualTo(TOMORROW),
                        or(
                                t -> t.longColumn("Quantity").isGreaterThanOrEqualTo(12),
                                t -> t.stringColumn("Customer").isEqualTo("Bob")
                        )
                )
        );

        System.out.println(priorityOrdersTomorrow);

        TablesawTestUtil.assertEquals(
                Table.create("orders")
                     .addColumns(
                             StringColumn.create(
                                     "Customer",
                                     "Dave", "Alice", "Bob"),
                             DateColumn.create(
                                     "DeliveryDate",
                                     TOMORROW, TOMORROW, TOMORROW),
                             StringColumn.create(
                                     "Donut",
                                     "Old Fashioned", "Jelly", "Pumpkin Spice"
                             ),
                             LongColumn.create("Quantity", 12, 12, 1)
                     ),
                priorityOrdersTomorrow
        );
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
