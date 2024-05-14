package dftalk.donuts.dfec;

import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class DonutDataFrameEcTest
{
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate YESTERDAY = TODAY.minusDays(1);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);
    
    private DataFrame customers;
    private DataFrame orders;
    private DataFrame menu;

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
                .addStringColumn("Description").addDoubleColumn("Price")
                .addRow("Blueberry", 1.25)
                .addRow("Glazed", 1.25)
                .addRow("Old Fashioned", 1.00)
                .addRow("Pumpkin Spice", 0.75)
                .addRow("Jelly", 1.50)
                .addRow("Apple Cider", 1.50)
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
    public void printEm()
    {
        System.out.println(this.customers);
        System.out.println(this.menu);
        System.out.println(this.orders);
    }
}
