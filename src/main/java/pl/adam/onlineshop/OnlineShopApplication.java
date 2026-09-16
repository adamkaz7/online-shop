package pl.adam.onlineshop;

import pl.adam.onlineshop.cli.ConsoleReader;
import pl.adam.onlineshop.cli.ShopCli;
import pl.adam.onlineshop.domain.cart.Cart;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.product.Computer;
import pl.adam.onlineshop.domain.product.Electronics;
import pl.adam.onlineshop.domain.product.Smartphone;
import pl.adam.onlineshop.repository.*;
import pl.adam.onlineshop.service.InvoiceGenerator;
import pl.adam.onlineshop.service.OrderProcessor;
import pl.adam.onlineshop.service.ProductManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OnlineShopApplication {
    public static void main(String[] args) {
        ProductRepository productRepository = new InMemoryProductRepository();
        OrderRepository orderRepository = new InMemoryOrderRepository();
        InvoiceRepository invoiceRepository = new InMemoryInvoiceRepository();
        ProductManager productManager = new ProductManager(productRepository);
        InvoiceGenerator invoiceGenerator = new InvoiceGenerator();

        OrderProcessor orderProcessor = new OrderProcessor(
                productRepository,
                orderRepository,
                invoiceRepository,
                invoiceGenerator
        );

        Customer customer = new Customer(
                UUID.fromString(
                        "00000000-0000-0000-0000-000000000001"
                ),
                "Jan Kowalski"
        );

        Cart cart = new Cart();
        ConsoleReader consoleReader = new ConsoleReader();

        addSampleProducts(productManager);

        ShopCli shopCli = new ShopCli(
                productManager,
                orderProcessor,
                customer,
                cart,
                consoleReader
        );

        shopCli.run();
    }

    private static void addSampleProducts(
            ProductManager productManager
    ) {
        productManager.addProduct(new Electronics(
                UUID.fromString(
                        "00000000-0000-0000-0000-000000000101"
                ),
                "Wireless Headphones",
                new BigDecimal("199.99"),
                10
        ));

        productManager.addProduct(new Computer(
                UUID.fromString(
                        "00000000-0000-0000-0000-000000000102"
                ),
                "Gaming Laptop",
                new BigDecimal("4999.99"),
                3,
                List.of("Intel i5", "Intel i7"),
                List.of(16, 32)
        ));

        productManager.addProduct(new Smartphone(
                UUID.fromString(
                        "00000000-0000-0000-0000-000000000103"
                ),
                "Smartphone Pro",
                new BigDecimal("3499.99"),
                5,
                List.of("Black", "Silver"),
                List.of(4000, 5000),
                List.of("Case", "Charger")
        ));
    }
}
