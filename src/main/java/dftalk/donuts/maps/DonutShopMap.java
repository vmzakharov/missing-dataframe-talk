package dftalk.donuts.maps;

import dftalk.donuts.domain.Customer;
import dftalk.donuts.domain.Donut;
import dftalk.donuts.domain.DonutShop;
import dftalk.donuts.domain.Order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DonutShopMap
{
    private List<Map<String, String>> customers = new ArrayList<>();
    private List<Map<String, Object>> menu = new ArrayList<>();
    private List<Map<String, Object>> orders = new ArrayList<>();

    public void createCustomer(String name, String street, String city, String state)
    {
        this.customers.add(
            Map.of("name", name, "street", street, "city", city, "state", state)
        );
    }

    public void createOrder(String customer, LocalDate deliveryDate, String donut, int quantity)
    {
        this.orders.add(
            Map.of("customer", customer, "deliveryDate", deliveryDate, "donut", donut, "quantity", quantity)
        );
    }

    public void createDonut(String donut, double price, double discountPrice)
    {
        this.menu.add(
            Map.of("donut", donut, "price", price, "discountPrice", discountPrice)
        );
    }

    public List<Map<String, String>> customers()
    {
        return this.customers;
    }

    public List<Map<String, Object>> menu()
    {
        return this.menu;
    }

    public List<Map<String, Object>> orders()
    {
        return this.orders;
    }
}
