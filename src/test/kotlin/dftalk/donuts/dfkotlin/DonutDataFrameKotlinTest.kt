package dftalk.donuts.dfkotlin

import org.jetbrains.kotlinx.dataframe.api.*
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

val TODAY: LocalDate = LocalDate.now()
val YESTERDAY: LocalDate = TODAY.minusDays(1)
val TOMORROW: LocalDate = TODAY.plusDays(1)

class DonutDataFrameKotlinTest {
    private val customers = dataFrameOf("Name", "Street", "City", "State")(
        "Alice", "902 S Pacific St", "Las Vegas", "NM",
        "Bob", "405 Main St", "Dallas", "SD",
        "Carol", "12300 State St", "Atlanta", "MI",
        "Dave", "102 S Main St", "Phoenix", "OR"
    );

    private val menu = dataFrameOf("Donut", "Price", "DiscountPrice")(
        "Blueberry", 1.25, 1.00,
        "Old Fashioned", 1.00, 0.90,
        "Pumpkin Spice", 0.75, 0.65,
        "Jelly", 1.50, 1.25,
        "Apple Cider", 1.50, 1.25
    );

    private val orders = dataFrameOf("Customer", "DeliveryDate", "Donut", "Quantity")(
        "Alice", YESTERDAY, "Old Fashioned", 12,
        "Alice", YESTERDAY, "Blueberry", 2,
        "Bob", YESTERDAY, "Old Fashioned", 12,
        "Alice", TODAY, "Apple Cider", 12,
        "Alice", TODAY, "Blueberry", 2,
        "Carol", TODAY, "Old Fashioned", 12,
        "Dave", TOMORROW, "Old Fashioned", 12,
        "Alice", TOMORROW, "Jelly", 12,
        "Carol", TOMORROW, "Blueberry", 2,
        "Bob", TOMORROW, "Pumpkin Spice", 1
    );

    @Test
    fun donutsInPopularityOrder() {
        val donutsInPopularityOrder = this.orders
            .groupBy("Donut")
            .sum("Quantity")
            .sortBy { it["Quantity"].desc() and it["Donut"] }
            .select("Donut")

//        println(donutsInPopularityOrder)

        assertEquals(
            dataFrameOf("Donut")(
                "Old Fashioned", "Apple Cider", "Jelly", "Blueberry", "Pumpkin Spice"
            ),
            donutsInPopularityOrder
        )
    }

    @Test
    fun priorityOrdersTomorrow() {
        val priorityOrdersTomorrow = orders.filter {
            ("DeliveryDate"<LocalDate>() == TOMORROW) && ("Quantity"<Int>() >= 12 || "Customer"<String>() == "Bob")
        }

//        println(priorityOrdersTomorrow);

        assertEquals(
            dataFrameOf("Customer", "DeliveryDate", "Donut", "Quantity")(
                "Dave", TOMORROW, "Old Fashioned", 12,
                "Alice", TOMORROW, "Jelly", 12,
                "Bob", TOMORROW, "Pumpkin Spice", 1
            ),
            priorityOrdersTomorrow
        )
    }

    @Test
    fun totalSpendPerCustomer() {
        val spendPerCustomer = orders
            .join(menu, "Donut")
            .add("OrderPrice") {
                (if ("Quantity"<Int>() < 12) "Price"<Double>() else "DiscountPrice"<Double>()) * "Quantity"<Int>()
            }
            .select("Customer", "OrderPrice")
            .groupBy("Customer")
            .sum {"OrderPrice"<Double>() named "Total Spend"}

//        println(spendPerCustomer)

        assertEquals(
            dataFrameOf("Customer", "Total Spend")(
                "Alice", 45.80,
                "Bob", 11.55,
                "Carol", 13.30,
                "Dave", 10.80
            ),
            spendPerCustomer
        )
    }

    @Test
    fun donutCountPerCustomerPerDay() {
        val donutsPerCustomerPerDay =
            orders
                .pivot("DeliveryDate", inward = false)
                .groupBy("Customer")
                .aggregate { sum("Quantity") }

//        println(donutsPerCustomerPerDay)

        assertEquals(
            dataFrameOf("Customer", YESTERDAY.toString(), TODAY.toString(), TOMORROW.toString())(
                "Alice", 14, 14, 12,
                "Bob", 12, null, 1,
                "Carol", null, 12, 2,
                "Dave", null, null, 12
            ),
            donutsPerCustomerPerDay
        )
    }
}