package dftalk.donuts.maps;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DonutShopMapTest
{
    protected static final LocalDate TODAY = LocalDate.now();
    protected static final LocalDate YESTERDAY = TODAY.minusDays(1);
    protected static final LocalDate TOMORROW = TODAY.plusDays(1);
    
    private static DonutShopMap DONUT_SHOP;

    @BeforeAll
    public static void setUpShop()
    {
        DONUT_SHOP = new DonutShopMap();

        DONUT_SHOP.createCustomer("Alice", "902 S Pacific St", "Las Vegas", "NM");
        DONUT_SHOP.createCustomer("Bob", "405 Main St", "Dallas", "SD");
        DONUT_SHOP.createCustomer("Carol", "12300 State St", "Atlanta", "MI");
        DONUT_SHOP.createCustomer("Dave", "102 S Main St", "Phoenix", "OR");

        DONUT_SHOP.createDonut("Blueberry", 1.25, 1.00);
        DONUT_SHOP.createDonut("Old Fashioned", 1.00, 0.90);
        DONUT_SHOP.createDonut("Pumpkin Spice", 0.75, 0.65);
        DONUT_SHOP.createDonut("Jelly", 1.50, 1.25);
        DONUT_SHOP.createDonut("Apple Cider", 1.50, 1.25);

        DONUT_SHOP.createOrder("Alice", YESTERDAY, "Old Fashioned", 12);
        DONUT_SHOP.createOrder("Alice", YESTERDAY, "Blueberry", 2);

        DONUT_SHOP.createOrder("Bob", YESTERDAY, "Old Fashioned", 12);

        DONUT_SHOP.createOrder("Alice", TODAY, "Apple Cider", 12);
        DONUT_SHOP.createOrder("Alice", TODAY, "Blueberry", 2);

        DONUT_SHOP.createOrder("Carol", TODAY, "Old Fashioned", 12);

        DONUT_SHOP.createOrder("Dave", TOMORROW, "Old Fashioned", 12);

        DONUT_SHOP.createOrder("Alice", TOMORROW, "Jelly", 12);
        DONUT_SHOP.createOrder("Carol", TOMORROW, "Blueberry", 2);

        DONUT_SHOP.createOrder("Bob", TOMORROW, "Pumpkin Spice", 1);
    }

    @Test
    public void donutsInPopularityOrder()
    {
        var donutsInPopularityOrder =
            DONUT_SHOP.orders()
                      .stream()
                      .collect(
                            Collectors.groupingBy(
                                    orderMap -> (String) orderMap.get("donut"),
                                    Collectors.summingInt(orderMap -> (int) orderMap.get("quantity")))
                      )
                      .entrySet()
                      .stream()
                      .sorted(
                              Map.Entry.<String, Integer>comparingByValue().reversed()
                              .thenComparing(Map.Entry.comparingByKey())
                      )
                      .map(Map.Entry::getKey)
                      .toList();

        assertEquals(
            List.of("Old Fashioned", "Apple Cider", "Jelly", "Blueberry", "Pumpkin Spice"),
            donutsInPopularityOrder);
    }
}
