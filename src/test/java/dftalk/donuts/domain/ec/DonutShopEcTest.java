package dftalk.donuts.domain.ec;

import dftalk.donuts.domain.Customer;
import dftalk.donuts.domain.Donut;
import dftalk.donuts.domain.DonutShopTestAbstract;
import dftalk.donuts.domain.Order;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.SortedMaps;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DonutShopEcTest
extends DonutShopTestAbstract
{
    private static DonutShopEc DONUT_SHOP;

    @BeforeAll
    static public void setUpShop()
    {
        DONUT_SHOP = new DonutShopEc();

        setUpShop(DONUT_SHOP);
    }

    @Test
    public void donutsInPopularityOrder()
    {
        MutableBag<Donut> donutCounts = Bags.mutable.of();

        DONUT_SHOP.orders()
                  .forEach(order -> donutCounts.addOccurrences(order.donut(), order.quantity()));

        MutableList<Donut> donutsInPopularityOrder = donutCounts
                .collectWithOccurrences(Tuples::pair, Lists.mutable.of())
                .sortThis(
                    Comparator
                        .comparingInt((Pair<Donut, Integer> p) -> p.getTwo()).reversed()
                        .thenComparing((Pair<Donut, Integer> p) -> p.getOne().description())
                )
                .collect(Pair::getOne);

        assertEquals(
                Lists.immutable.of(OLD_FASHIONED, APPLE_CIDER, JELLY, BLUEBERRY, PUMPKIN_SPICE),
                donutsInPopularityOrder);
    }

    @Test
    public void priorityOrdersForTomorrow()
    {
        MutableSet<Order> priorityOrdersForTomorrow =
                DONUT_SHOP.orders()
                        .select(order ->
                            order.deliveryDate().equals(TOMORROW)
                            &&
                            (order.quantity() >= 12 || order.customer().equals(BOB))
                        )
                        .toSet();

        assertEquals(
                Sets.immutable.of(
                        new Order(DAVE, TOMORROW, OLD_FASHIONED, 12),
                        new Order(ALICE, TOMORROW, JELLY, 12),
                        new Order(BOB, TOMORROW, PUMPKIN_SPICE, 1)
                ),
                priorityOrdersForTomorrow);
    }

    @Test
    public void totalSpendPerCustomer()
    {
        MapIterable<Customer, Double> totalSpendPerCustomer =
            DONUT_SHOP.orders()
                      .aggregateBy(
                          Order::customer,
                          () -> 0.0,
                          (total, order) -> total
                                  + order.quantity() * (order.quantity() < 12 ? order.donut().price() : order.donut().discountPrice()),
                          SortedMaps.mutable.of(Comparator.comparing(Customer::name))
                      );

        SortedMap<Customer, Double> expected = SortedMaps.mutable.of(Comparator.comparing(Customer::name));
        expected.put(ALICE, 45.80);
        expected.put(BOB, 11.55);
        expected.put(CAROL, 13.30);
        expected.put(DAVE, 10.80);

        assertEquals(expected, totalSpendPerCustomer);
    }
}
