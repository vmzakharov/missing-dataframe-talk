package dftalk.donuts.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DonutShop
{
    private final List<Customer> customers = new ArrayList<>();
    private final List<Donut> donuts = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();

    public Customer createCustomer(String name, String street, String city, String state)
    {
        Customer customer = new Customer(name, street, city, state);
        this.customers.add(customer);
        return customer;
    }

    public Order createOrder(Customer customer, LocalDate deliveryDate, Donut donut, int quantity)
    {
        Order order = new Order(customer, deliveryDate, donut, quantity);
        this.orders.add(order);
        return order;
    }

    public Donut bakeDonuts(String description, double price, double discountPrice)
    {
        Donut donut = new Donut(description, price, discountPrice);
        this.donuts.add(donut);
        return donut;
    }

    public List<Order> orders()
    {
        return this.orders;
    }

    public List<Donut> donuts()
    {
        return this.donuts;
    }
}
