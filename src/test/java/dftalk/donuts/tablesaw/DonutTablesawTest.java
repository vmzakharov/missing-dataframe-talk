package dftalk.donuts.tablesaw;

import dftalk.util.tablesaw.TablesawTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.*;

import java.time.LocalDate;

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
                             StringColumn.create(
                                     "Donut",
                                     "Blueberry", "Old Fashioned", "Pumpkin Spice", "Jelly", "Apple Cider"),
                             DoubleColumn.create(
                                     "Price",
                                     1.25, 1.00, 0.75, 1.50, 1.50),
                             DoubleColumn.create(
                                     "DiscountPrice",
                                     1.00, 0.90, 0.65, 1.25, 1.25)
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
                                     "Alice", "Alice", "Bob", "Alice", "Alice", "Carol", "Dave", "Alice", "Carol", "Bob"),
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

        System.out.println(this.orders
                .summarize("Quantity", sum)
                .by("Donut")
                .sortOn("-Sum [Quantity]", "Donut"));

        TablesawTestUtil.assertEquals(
            Table.create("expected")
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
                             StringColumn.create("Customer", "Dave", "Alice", "Bob"),
                             DateColumn.create("DeliveryDate", TOMORROW, TOMORROW, TOMORROW),
                             StringColumn.create("Donut", "Old Fashioned", "Jelly", "Pumpkin Spice"),
                             LongColumn.create("Quantity", 12, 12, 1)
                     ),
                priorityOrdersTomorrow
        );
    }

    @Test
    public void totalSpendPerCustomer1()
    {
        Table ordersWithPrices = this.orders
                .joinOn("Donut")
                .inner(this.menu);

        System.out.println(ordersWithPrices);

        DoubleColumn orderPrice = DoubleColumn.create("OrderPrice");

        ordersWithPrices.addColumns(orderPrice);

        LongColumn quantity = ordersWithPrices.longColumn("Quantity");

        orderPrice.set(
                quantity.isLessThan(12),
                ordersWithPrices.doubleColumn("Price").multiply(ordersWithPrices.longColumn("Quantity"))
        );
        orderPrice.set(
                quantity.isGreaterThanOrEqualTo(12),
                ordersWithPrices.doubleColumn("DiscountPrice").multiply(ordersWithPrices.longColumn("Quantity"))
        );

        Table spendPerCustomer = ordersWithPrices
                .summarize("OrderPrice", sum)
                .by("Customer")
                .sortOn("Customer");

        System.out.println(spendPerCustomer);

        TablesawTestUtil.assertEquals(
                Table.create("expected")
                        .addColumns(
                            StringColumn.create("Customer", "Alice", "Bob", "Carol", "Dave"),
                            DoubleColumn.create("Sum [OrderPrice]", 45.8, 11.55, 13.3, 10.8)
                        ),
                spendPerCustomer
        );
    }

    @Test
    public void totalSpendPerCustomer2()
    {
        Table ordersWithPrices = this.orders
                .joinOn("Donut")
                .inner(this.menu);

        System.out.println(ordersWithPrices);

        DoubleColumn orderPrice = DoubleColumn.create("OrderPrice");

        ordersWithPrices.forEach(
                row -> {
                    long quantity = row.getLong("Quantity");
                    orderPrice.append(
                        quantity < 12
                                ? quantity * row.getDouble("Price")
                                : quantity * row.getDouble("DiscountPrice")
                    );
                }
        );

        ordersWithPrices.addColumns(orderPrice);

        Table spendPerCustomer = ordersWithPrices
                .summarize("OrderPrice", sum)
                .by("Customer")
                .sortOn("Customer");

        System.out.println(spendPerCustomer);

        TablesawTestUtil.assertEquals(
                Table.create("expected")
                        .addColumns(
                            StringColumn.create("Customer", "Alice", "Bob", "Carol", "Dave"),
                            DoubleColumn.create("Sum [OrderPrice]", 45.8, 11.55, 13.3, 10.8)
                        ),
                spendPerCustomer
        );
    }

    @Test
    public void donutCountPerCustomerPerDay()
    {
        Table donutsPerCustomerPerDay = this.orders.pivot("Customer", "DeliveryDate", "Quantity", sum);

        System.out.println(donutsPerCustomerPerDay);

        Table expected = Table.create("expected")
              .addColumns(
                      StringColumn.create("Customer", "Alice", "Bob", "Carol", "Dave"),
                      DoubleColumn.create(YESTERDAY.toString(), 14.0, 12.0, 0.0, 0.0),
                      DoubleColumn.create(TODAY.toString(), 14.0, 0.0, 12.0, 0.0),
                      DoubleColumn.create(TOMORROW.toString(), 12.0, 1.0, 2.0, 12.0)
              );

        expected.column(YESTERDAY.toString()).setMissing(2);
        expected.column(YESTERDAY.toString()).setMissing(3);
        expected.column(TODAY.toString()).setMissing(1);
        expected.column(TODAY.toString()).setMissing(3);

        TablesawTestUtil.assertEquals(
                expected,
                donutsPerCustomerPerDay
        );

    }
}
