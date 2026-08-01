package pl.adam.onlineshop.cli;

import lombok.NonNull;
import pl.adam.onlineshop.domain.cart.Cart;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.Order;
import pl.adam.onlineshop.domain.order.OrderItem;
import pl.adam.onlineshop.domain.product.Product;
import pl.adam.onlineshop.domain.promotion.Promotion;
import pl.adam.onlineshop.exception.InsufficientStockException;
import pl.adam.onlineshop.exception.InvoiceFileException;
import pl.adam.onlineshop.exception.ProductNotFoundException;
import pl.adam.onlineshop.exception.PromotionNotFoundException;
import pl.adam.onlineshop.persistence.InvoiceFileWriter;
import pl.adam.onlineshop.service.OrderProcessor;
import pl.adam.onlineshop.service.ProductManager;
import pl.adam.onlineshop.service.PromotionService;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class ShopCli {
    private final ProductManager productManager;
    private final OrderProcessor orderProcessor;
    private final PromotionService promotionService;
    private final InvoiceFileWriter invoiceFileWriter;
    private final Customer customer;
    private final Cart cart;
    private final ConsoleReader consoleReader;

    public ShopCli(
            @NonNull ProductManager productManager,
            @NonNull OrderProcessor orderProcessor,
            @NonNull PromotionService promotionService,
            @NonNull InvoiceFileWriter invoiceFileWriter,
            @NonNull Customer customer,
            @NonNull Cart cart,
            @NonNull ConsoleReader consoleReader
    ) {
        this.productManager = productManager;
        this.orderProcessor = orderProcessor;
        this.promotionService = promotionService;
        this.invoiceFileWriter = invoiceFileWriter;
        this.customer = customer;
        this.cart = cart;
        this.consoleReader = consoleReader;
    }

    public void run() {
        int selectOption;

        do {
            printMenu();
            selectOption = consoleReader.readInt("Select option: ");
            handleOption(selectOption);
        } while (selectOption != 0);
    }

    private void printMenu() {
        System.out.println();
        System.out.println("ONLINE SHOP");
        System.out.println("1. Show products");
        System.out.println("2. Add product to cart");
        System.out.println("3. Show cart");
        System.out.println("4. Place order");
        System.out.println("0. Exit");
    }

    private void handleOption(int selectOption) {
        switch (selectOption) {
            case 1 -> showProducts();
            case 2 -> addProductToCart();
            case 3 -> showCart();
            case 4 -> placeOrder();
            case 0 -> System.out.println("Goodbye!");
            default -> System.out.println("Unknown option.");
        }
    }

    private void showProducts() {
        List<Product> products = productManager.getAllProducts();

        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        System.out.println();
        System.out.println("Available products:");

        for (int index = 0; index < products.size(); index++) {
            System.out.println((index + 1) + ". " + products.get(index));
        }
    }

    private void addProductToCart() {
        List<Product> products = productManager.getAllProducts();

        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        System.out.println();
        System.out.println("Select product:");

        for (int index = 0; index < products.size(); index++) {
            System.out.println((index + 1) + ". " + products.get(index));
        }

        int productNumber = consoleReader.readInt("Enter product number: ");
        if (productNumber < 1 || productNumber > products.size()) {
            System.out.println("Invalid product number.");
            return;
        }

        int quantity = consoleReader.readInt("Enter quantity: ");

        Product selectedProduct = products.get(productNumber - 1);

        try {
            cart.addProduct(selectedProduct, quantity);
            System.out.println("Product added to cart.");
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void showCart() {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        System.out.println();
        System.out.println("Your cart:");

        cart.getItems().forEach(System.out::println);

        System.out.println("Total quantity: " + cart.getTotalQuantity());
    }

    private void placeOrder() {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> new OrderItem(
                        cartItem.getProduct().getId(),
                        cartItem.getProduct().getName(),
                        cartItem.getProduct().getPrice(),
                        cartItem.getQuantity()
                ))
                .toList();

        Order order = new Order(
                UUID.randomUUID(),
                customer,
                orderItems
        );

        try {
            applyPromotion(order);

            Invoice invoice = orderProcessor.process(order);

            cart.clearCart();

            System.out.println();
            System.out.println("Order placed.");
            System.out.println(order);
            System.out.println();
            System.out.println(invoice);

            saveInvoiceToFile(invoice);
        } catch (
                PromotionNotFoundException
                | ProductNotFoundException
                | InsufficientStockException
                | IllegalArgumentException exception) {
            System.out.println("Order could not be processed: " + exception.getMessage());
        }
    }

    private void applyPromotion(Order order) {
        String promotionCode = consoleReader.readLine("Enter promotion code or press enter to skip: ");

        if (promotionCode == null || promotionCode.isBlank()) {
            return;
        }

        Promotion promotion = promotionService.findPromotionByCode(promotionCode);
        order.applyPromotion(promotion);
        System.out.println("Promotion applied: " + promotion.getCode());
    }

    private void saveInvoiceToFile(Invoice invoice) {
        try {
            Path invoicePath = invoiceFileWriter.write(invoice);

            System.out.println("Invoice saved to: " + invoicePath);
        } catch (InvoiceFileException exception) {
            System.out.println("Order was placed, but invoice file could not be saved: " + exception.getMessage());
        }
    }
}
