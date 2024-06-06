package dftalk.donuts.domain;

import dftalk.donuts.domain.jdk.DonutShopJdk;
import org.junit.jupiter.api.BeforeAll;

import java.time.LocalDate;

public class DonutShopTestAbstract
{
    protected static Customer ALICE;
    protected static Customer BOB;
    protected static Customer CAROL;
    protected static Customer DAVE;

    protected static Donut BLUEBERRY;
    protected static Donut OLD_FASHIONED;
    protected static Donut PUMPKIN_SPICE;
    protected static Donut JELLY;
    protected static Donut APPLE_CIDER;

    protected static final LocalDate TODAY = LocalDate.now();
    protected static final LocalDate YESTERDAY = TODAY.minusDays(1);
    protected static final LocalDate TOMORROW = TODAY.plusDays(1);

    static public void setUpShop(DonutShop donutShop)
    {
        ALICE = donutShop.createCustomer("Alice", "902 S Pacific St", "Las Vegas", "NM");
        BOB = donutShop.createCustomer("Bob", "405 Main St", "Dallas", "SD");
        CAROL = donutShop.createCustomer("Carol", "12300 State St", "Atlanta", "MI");
        DAVE = donutShop.createCustomer("Dave", "102 S Main St", "Phoenix", "OR");

        BLUEBERRY = donutShop.createDonut("Blueberry", 1.25, 1.00);
        OLD_FASHIONED = donutShop.createDonut("Old Fashioned", 1.00, 0.90);
        PUMPKIN_SPICE = donutShop.createDonut("Pumpkin Spice", 0.75, 0.65);
        JELLY = donutShop.createDonut("Jelly", 1.50, 1.25);
        APPLE_CIDER = donutShop.createDonut("Apple Cider", 1.50, 1.25);

        donutShop.createOrder(ALICE, YESTERDAY, OLD_FASHIONED, 12);
        donutShop.createOrder(ALICE, YESTERDAY, BLUEBERRY, 2);

        donutShop.createOrder(BOB, YESTERDAY, OLD_FASHIONED, 12);

        donutShop.createOrder(ALICE, TODAY, APPLE_CIDER, 12);
        donutShop.createOrder(ALICE, TODAY, BLUEBERRY, 2);

        donutShop.createOrder(CAROL, TODAY, OLD_FASHIONED, 12);

        donutShop.createOrder(DAVE, TOMORROW, OLD_FASHIONED, 12);

        donutShop.createOrder(ALICE, TOMORROW, JELLY, 12);
        donutShop.createOrder(CAROL, TOMORROW, BLUEBERRY, 2);

        donutShop.createOrder(BOB, TOMORROW, PUMPKIN_SPICE, 1);
    }
}
