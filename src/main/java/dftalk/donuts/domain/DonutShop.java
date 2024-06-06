package dftalk.donuts.domain;

import java.time.LocalDate;

public interface DonutShop
{
    Customer createCustomer(String name, String street, String city, String state);

    Order createOrder(Customer customer, LocalDate deliveryDate, Donut donut, int quantity);

    Donut createDonut(String description, double price, double discountPrice);
}
