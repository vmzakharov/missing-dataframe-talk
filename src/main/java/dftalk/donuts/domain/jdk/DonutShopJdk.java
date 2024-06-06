package dftalk.donuts.domain.jdk;

import dftalk.donuts.domain.Customer;
import dftalk.donuts.domain.Donut;
import dftalk.donuts.domain.DonutShop;
import dftalk.donuts.domain.Order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DonutShopJdk
implements DonutShop
{
    private final List<Customer> customers = new ArrayList<>();
    private final List<Donut> donuts = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();

    @Override
    public Customer createCustomer(String name, String street, String city, String state)
    {
        Customer customer = new Customer(name, street, city, state);
        this.customers.add(customer);
        return customer;
    }

    @Override
    public Order createOrder(Customer customer, LocalDate deliveryDate, Donut donut, int quantity)
    {
        Order order = new Order(customer, deliveryDate, donut, quantity);
        this.orders.add(order);
        return order;
    }

    @Override
    public Donut createDonut(String description, double price, double discountPrice)
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
