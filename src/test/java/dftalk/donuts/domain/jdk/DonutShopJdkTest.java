package dftalk.donuts.domain.jdk;

import dftalk.donuts.domain.Customer;
import dftalk.donuts.domain.Donut;
import dftalk.donuts.domain.DonutShopTestAbstract;
import dftalk.donuts.domain.Order;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DonutShopJdkTest
extends DonutShopTestAbstract
{
    private static DonutShopJdk DONUT_SHOP;

    @BeforeAll
    static public void setUpShop()
    {
        DONUT_SHOP = new DonutShopJdk();

        setUpShop(DONUT_SHOP);
    }

    @Test
    public void donutsInPopularityOrder()
    {
        var donutsInPopularityOrder = DONUT_SHOP.orders().stream()
            .collect(
                  Collectors.groupingBy(
                      Order::donut,
                      Collectors.summingInt(Order::quantity))
                )
            .entrySet()
            .stream()
            .sorted(
                    Map.Entry.<Donut, Integer>comparingByValue().reversed()
                    .thenComparing((Map.Entry<Donut, Integer> m) -> m.getKey().description())
            )
            .map(Map.Entry::getKey)
            .toList();

        assertEquals(
            List.of(OLD_FASHIONED, APPLE_CIDER, JELLY, BLUEBERRY, PUMPKIN_SPICE),
                donutsInPopularityOrder);
    }

    @Test
    public void priorityOrdersForTomorrow()
    {
        Set<Order>  priorityOrdersForTomorrow =
                DONUT_SHOP.orders()
                     .stream()
                     .filter(order ->
                                 order.deliveryDate().equals(TOMORROW)
                                 &&
                                 (order.quantity() >= 12 || order.customer().equals(BOB))
                     )
                     .collect(Collectors.toSet());

        Set<Order> expected = Set.of(
            new Order(DAVE, TOMORROW, OLD_FASHIONED, 12),
            new Order(ALICE, TOMORROW, JELLY, 12),
            new Order(BOB, TOMORROW, PUMPKIN_SPICE, 1)
        );

        assertEquals(expected, priorityOrdersForTomorrow);
    }

    @Test
    public void totalSpendPerCustomer()
    {
        Map<Customer, Double> totalSpendPerCustomer = DONUT_SHOP.orders()
                .stream()
                .collect(
                    Collectors.groupingBy(
                        Order::customer,
                        ()-> new TreeMap<>(Comparator.comparing(Customer::name)),
                        Collectors.summingDouble(order ->
                            order.quantity()
                                    * (order.quantity() < 12 ? order.donut().price() : order.donut().discountPrice())
                        )
                    )
                );

        TreeMap<Customer, Double> expected = new TreeMap<>(Comparator.comparing(Customer::name));
        expected.put(ALICE, 45.80);
        expected.put(BOB, 11.55);
        expected.put(CAROL, 13.30);
        expected.put(DAVE, 10.80);

        assertEquals(expected, totalSpendPerCustomer);
    }
}
