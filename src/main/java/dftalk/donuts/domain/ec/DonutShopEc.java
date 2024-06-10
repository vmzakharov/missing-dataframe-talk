package dftalk.donuts.domain.ec;

import dftalk.donuts.domain.Customer;
import dftalk.donuts.domain.Donut;
import dftalk.donuts.domain.DonutShop;
import dftalk.donuts.domain.Order;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;

import java.time.LocalDate;

public class DonutShopEc
implements DonutShop
{
    private final MutableList<Customer> customers = Lists.mutable.empty();
    private final MutableList<Donut> donuts = Lists.mutable.empty();
    private final MutableList<Order> orders = Lists.mutable.empty();

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

    public ListIterable<Order> orders()
    {
        return this.orders;
    }

    public ListIterable<Donut> donuts()
    {
        return this.donuts;
    }
}
