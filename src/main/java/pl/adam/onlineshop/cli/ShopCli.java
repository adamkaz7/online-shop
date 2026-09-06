package pl.adam.onlineshop.cli;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class ShopCli {
    private static final int EXIT_OPTION = 0;
    private static final int SHOW_PRODUCTS_OPTION = 1;
    private static final int ADD_PRODUCT_TO_CART_OPTION = 2;
    private static final int SHOW_CART_OPTION = 3;
    private static final int PLACE_ORDER_OPTION = 4;

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
        int selectedOption;

        do {
            displayMenu();
            selectedOption = consoleReader.readInt("Select option: ");
            executeSelectedOption(selectedOption);
        } while (selectedOption != EXIT_OPTION);
    }

    private void displayMenu() {
        log.info("ONLINE SHOP");
        log.info("{}. Show products", SHOW_PRODUCTS_OPTION);
        log.info("{}. Add product to cart", ADD_PRODUCT_TO_CART_OPTION);
        log.info("{}. Show cart", SHOW_CART_OPTION);
        log.info("{}. Place order", PLACE_ORDER_OPTION);
        log.info("{}. Exit", EXIT_OPTION);
    }

    private void executeSelectedOption(int selectedOption) {
        switch (selectedOption) {
            case SHOW_PRODUCTS_OPTION -> showProducts();
            case ADD_PRODUCT_TO_CART_OPTION -> addProductToCart();
            case SHOW_CART_OPTION -> showCart();
            case PLACE_ORDER_OPTION -> placeOrder();
            case EXIT_OPTION -> log.info("Goodbye!");
            default -> log.warn("Unknown option.");
        }
    }

    private void showProducts() {
        List<Product> products = productManager.getAllProducts();

        if (products.isEmpty()) {
            log.warn("No products available.");
            return;
        }

        log.info("Available products:");

        for (int index = 0; index < products.size(); index++) {
            log.info("{}. {}", index + 1, products.get(index));
        }
    }

    private void addProductToCart() {
        List<Product> products = productManager.getAllProducts();

        if (products.isEmpty()) {
            log.warn("No products available.");
            return;
        }

        log.info("Select product:");

        for (int index = 0; index < products.size(); index++) {
            log.info("{}. {}", index + 1, products.get(index));
        }

        int productNumber = consoleReader.readInt("Enter product number: ");
        if (productNumber < 1 || productNumber > products.size()) {
            log.warn("Invalid product number.");
            return;
        }

        int quantity = consoleReader.readInt("Enter quantity: ");

        Product selectedProduct = products.get(productNumber - 1);

        try {
            validateCartQuantity(selectedProduct, quantity);

            cart.addProduct(selectedProduct, quantity);
            log.info("Product added to cart.");
        } catch (IllegalArgumentException exception) {
            log.warn("Could not add product to cart: {}", exception.getMessage());
        }
    }

    private void validateCartQuantity(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        int quantityAlreadyInCart = cart
                .findItemByProductId(product.getId())
                .map(item -> item.getQuantity())
                .orElse(0);

        int totalRequestedQuantity = quantityAlreadyInCart + quantity;

        if (!product.hasAvailableQuantity(totalRequestedQuantity)) {
            throw new IllegalArgumentException(
                    "Only "
                    + product.getAvailableQuantity()
                    + " items are available"
            );
        }
    }

    private void showCart() {
        if (cart.isEmpty()) {
            log.warn("Cart is empty.");
            return;
        }

        log.info("Your cart:");

        cart.getItems().forEach(item -> log.info("{}", item));

        log.info("Total quantity: {}", cart.getTotalQuantity());
    }

    private void placeOrder() {
        if (cart.isEmpty()) {
            log.warn("Cart is empty.");
            return;
        }

        Order order = createOrderFromCart();

        try {
            applyPromotion(order);

            Invoice invoice = orderProcessor.process(order);

            cart.clearCart();

            log.info("Order placed.");
            log.info("Order details: {}", order);
            log.info("Invoice details: {}", invoice);

            saveInvoiceToFile(invoice);
        } catch (
                PromotionNotFoundException
                | ProductNotFoundException
                | InsufficientStockException
                | IllegalArgumentException exception) {
            log.warn("Order could not be processed: {}", exception.getMessage());
        }
    }

    private Order createOrderFromCart() {
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> new OrderItem(
                        cartItem.getProduct().getId(),
                        cartItem.getProduct().getName(),
                        cartItem.getProduct().getPrice(),
                        cartItem.getQuantity()
                ))
                .toList();

        return new Order(
                UUID.randomUUID(),
                customer,
                orderItems
        );
    }

    private void applyPromotion(Order order) {
        String promotionCode = consoleReader.readLine("Enter promotion code or press enter to skip: ");

        if (promotionCode == null || promotionCode.isBlank()) {
            return;
        }

        Promotion promotion = promotionService.findPromotionByCode(promotionCode);
        order.applyPromotion(promotion);
        log.info("Promotion applied: {}", promotion.getCode());
    }

    private void saveInvoiceToFile(Invoice invoice) {
        try {
            Path invoicePath = invoiceFileWriter.write(invoice);

            log.info("Invoice saved to: {}", invoicePath);
        } catch (InvoiceFileException exception) {
            log.error(
                    "Order was placed, but invoice file could not be saved: {}",
                    exception.getMessage(),
                    exception
            );
        }
    }
}
