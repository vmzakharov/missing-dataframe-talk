package dftalk.donuts.collections;

import dftalk.donuts.domain.Customer;
import dftalk.donuts.domain.Donut;
import dftalk.donuts.domain.DonutShop;
import dftalk.donuts.domain.Order;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DonutCollectionsTest
{
    private static DonutShop DONUT_SHOP;

    private static Customer ALICE;
    private static Customer BOB;
    private static Customer CAROL;
    private static Customer DAVE;

    private static Donut BLUEBERRY;
    private static Donut GLAZED;
    private static Donut OLD_FASHIONED;
    private static Donut PUMPKIN_SPICE;
    private static Donut JELLY;
    private static Donut APPLE_CIDER;

    private static LocalDate TODAY = LocalDate.now();
    private static LocalDate YESTERDAY = TODAY.minusDays(1);
    private static LocalDate TOMORROW = TODAY.plusDays(1);

    @BeforeAll
    static public void setUpShop()
    {
        ALICE = DONUT_SHOP.createCustomer("Alice", "902 S Pacific St", "Las Vegas", "NM");
        BOB = DONUT_SHOP.createCustomer("Bob", "405 Main St", "Dallas", "SD");
        CAROL = DONUT_SHOP.createCustomer("Carol", "12300 State St", "Atlanta", "MI");
        DAVE = DONUT_SHOP.createCustomer("Dave", "102 S Main St", "Phoenix", "OR");

        BLUEBERRY = DONUT_SHOP.bakeDonuts("Blueberry", 1.25);
        GLAZED = DONUT_SHOP.bakeDonuts("Glazed", 1.25);
        OLD_FASHIONED = DONUT_SHOP.bakeDonuts("Old Fashioned", 1.00);
        PUMPKIN_SPICE = DONUT_SHOP.bakeDonuts("Pumpkin Spice", 0.75);
        JELLY = DONUT_SHOP.bakeDonuts("Jelly", 1.50);
        APPLE_CIDER = DONUT_SHOP.bakeDonuts("Apple Cider", 1.50);

        DONUT_SHOP.createOrder(ALICE, YESTERDAY, OLD_FASHIONED, 12);
        DONUT_SHOP.createOrder(ALICE, YESTERDAY, BLUEBERRY, 2);

        DONUT_SHOP.createOrder(BOB,   YESTERDAY, OLD_FASHIONED, 12);

        DONUT_SHOP.createOrder(ALICE, TODAY,     APPLE_CIDER, 12);
        DONUT_SHOP.createOrder(ALICE, TODAY,     BLUEBERRY, 2);

        DONUT_SHOP.createOrder(CAROL, TODAY,     OLD_FASHIONED, 12);

        DONUT_SHOP.createOrder(DAVE,  TOMORROW,  OLD_FASHIONED, 12);

        DONUT_SHOP.createOrder(ALICE, TOMORROW,  JELLY, 12);
        DONUT_SHOP.createOrder(ALICE, TOMORROW,  BLUEBERRY, 2);

        DONUT_SHOP.createOrder(BOB,   TOMORROW,  PUMPKIN_SPICE, 1);
    }

    @Test
    public void bestSellers()
    {
        Long blorp = DONUT_SHOP.orders()
                               .stream()
                               .collect(Collectors.counting());
    }

    public Donut findMostPopularDonut(List<Order> orders)
    {
        return BLUEBERRY;
    }

    @Test
    public void totalSpendPerCustomer()
    {
        DONUT_SHOP.orders();
    }
}
