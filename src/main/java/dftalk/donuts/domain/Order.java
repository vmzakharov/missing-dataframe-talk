package dftalk.donuts.domain;

import java.time.LocalDate;

public record Order(Customer customer, LocalDate deliveryDate, Donut donut, int quantity)
{
}
