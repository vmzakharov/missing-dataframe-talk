package dftalk.donuts.domain;

import java.time.LocalDate;
import java.util.Map;

public record Order(Customer customer, LocalDate deliveryDate, Map<Donut, Integer> orderItems)
{
}
