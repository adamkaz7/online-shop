package pl.adam.onlineshop.service;

import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.Order;

public record OrderResult(Order order, Invoice invoice, Throwable error) {
    public static OrderResult success(Order order, Invoice invoice) {
        return new OrderResult(order, invoice, null);
    }

    public static OrderResult failure(Order order, Throwable error) {
        return new OrderResult(order, null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }
}
