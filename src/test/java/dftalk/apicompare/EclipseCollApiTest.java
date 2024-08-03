package dftalk.apicompare;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.partition.list.PartitionList;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EclipseCollApiTest
{
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate YESTERDAY = TODAY.minusDays(1);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);

    private ListIterable<SimpleOrder> donutOrders;

    @BeforeEach
    public void setupDonutOrders()
    {
        this.donutOrders = Lists.immutable.of(
            new SimpleOrder("Alice", YESTERDAY, "Old Fashioned",  12,  10.80),
            new SimpleOrder("Alice", YESTERDAY, "Blueberry",       2,   2.50),
            new SimpleOrder("Bob",   YESTERDAY, "Old Fashioned",  12,  10.80),
            new SimpleOrder("Alice", TODAY,     "Apple Cider",    12,  15.00),
            new SimpleOrder("Alice", TODAY,     "Blueberry",       2,   2.50),
            new SimpleOrder("Carol", TODAY,     "Old Fashioned",  12,  10.80),
            new SimpleOrder("Dave",  TOMORROW,  "Old Fashioned",  12,  10.80),
            new SimpleOrder("Alice", TOMORROW,  "Jelly",          12,  15.00),
            new SimpleOrder("Carol", TOMORROW,  "Blueberry",       2,   2.50),
            new SimpleOrder("Bob",   TOMORROW,  "Pumpkin Spice",   1,   0.75)
        );
    }

    @Test
    public void partition()
    {
        PartitionList<SimpleOrder> smallAndBigOrders = this.donutOrders.partition(simpleOrder -> simpleOrder.quantity < 12);

        assertEquals(4, smallAndBigOrders.getSelected().size());
        assertEquals(6, smallAndBigOrders.getRejected().size());
    }

    @Test
    public void distinct()
    {
        ListIterable<SimpleOrder> x = this.donutOrders.distinct(
                HashingStrategies.fromFunctions(SimpleOrder::customer, SimpleOrder::deliveryDate));
        assertEquals(8, x.size());
    }

    @Test
    public void select()
    {
        assertEquals(
                Lists.immutable.of(
                        new SimpleOrder("Carol", TODAY,     "Old Fashioned",  12,  10.80),
                        new SimpleOrder("Carol", TOMORROW,  "Blueberry",       2,   2.50)
                ),
                this.donutOrders.select(order -> order.customer().equals("Carol"))
        );
    }

    @Test
    public void reject()
    {
        assertEquals(
                Lists.immutable.of(
                        new SimpleOrder("Bob",   YESTERDAY, "Old Fashioned",  12,  10.80),
                        new SimpleOrder("Carol", TODAY,     "Old Fashioned",  12,  10.80),
                        new SimpleOrder("Dave",  TOMORROW,  "Old Fashioned",  12,  10.80),
                        new SimpleOrder("Carol", TOMORROW,  "Blueberry",       2,   2.50),
                        new SimpleOrder("Bob",   TOMORROW,  "Pumpkin Spice",   1,   0.75)
                ),
                this.donutOrders.reject(order -> order.customer().equals("Alice"))
        );

    }

    @Test
    public void sort()
    {
        MutableList<SimpleOrder> sorted =
            this.donutOrders.toList()
                            .sortThis(
                                    Comparator.comparing(SimpleOrder::customer)
                                              .thenComparingDouble(SimpleOrder::orderPrice)
                            );

        assertEquals(
                "Alice:2.5;Alice:2.5;Alice:10.8;Alice:15.0;Alice:15.0;Bob:0.75;Bob:10.8;Carol:2.5;Carol:10.8;Dave:10.8",
                sorted.collect(order -> order.customer + ":" + order.orderPrice).makeString(";"));
    }

    @Test
    public void inject()
    {
        String customerLine = this.donutOrders.injectInto(
                "Head", (aggregate, current) -> aggregate + "->" + current.customer
        );

        assertEquals("Head->Alice->Alice->Bob->Alice->Alice->Carol->Dave->Alice->Carol->Bob", customerLine);
    }

    record SimpleOrder(String customer, LocalDate deliveryDate, String donut, int quantity, double orderPrice)
    {}
}
