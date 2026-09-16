package pl.adam.onlineshop.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.adam.onlineshop.domain.cart.Cart;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.Order;
import pl.adam.onlineshop.domain.product.Electronics;
import pl.adam.onlineshop.domain.product.Product;
import pl.adam.onlineshop.domain.promotion.Promotion;
import pl.adam.onlineshop.exception.InsufficientStockException;
import pl.adam.onlineshop.exception.InvoiceFileException;
import pl.adam.onlineshop.exception.PromotionNotFoundException;
import pl.adam.onlineshop.persistence.InvoiceFileWriter;
import pl.adam.onlineshop.service.OrderProcessor;
import pl.adam.onlineshop.service.ProductManager;
import pl.adam.onlineshop.service.PromotionService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShopCliTest {
    private static final Path INVOICE_PATH = Path.of(
            "data",
            "invoices",
            "invoice-test.txt");

    private static final String PROMOTION = "Enter promotion code or press enter to skip: ";

    private static final UUID CUSTOMER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000001"
    );

    private static final UUID PRODUCT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000101"
    );

    @Mock
    private ProductManager productManager;

    @Mock
    private OrderProcessor orderProcessor;

    @Mock
    private PromotionService promotionService;

    @Mock
    private ConsoleReader consoleReader;

    @Mock
    private Invoice invoice;

    @Mock
    private InvoiceFileWriter invoiceFileWriter;

    private Cart cart;
    private Product product;
    private ShopCli shopCli;

    @BeforeEach
    public void setUp() {
        cart = new Cart();

        product = new Electronics(
                PRODUCT_ID,
                "Laptop",
                new BigDecimal("199.99"),
                10
        );

        Customer customer = new Customer(
                CUSTOMER_ID,
                "Jan Kowalski"
        );

        shopCli = new ShopCli(
                productManager,
                orderProcessor,
                promotionService,
                invoiceFileWriter,
                customer,
                cart,
                consoleReader
        );
    }

    @Test
    @DisplayName("Should show product when option is selected")
    public void shouldShowProductWhenOptionIsSelected() {
        // Arrange
        when(consoleReader.readInt("Select option: ")).thenReturn(1, 0);
        when(productManager.getAllProducts()).thenReturn(List.of(product));

        // Act
        shopCli.run();

        // Assert
        verify(productManager).getAllProducts();
    }

    @Test
    @DisplayName("Should add selected product to cart")
    public void shouldAddSelectedProductToCart() {
        // Arrange
        when(consoleReader.readInt("Select option: ")).thenReturn(2, 0);
        when(consoleReader.readInt("Enter product number: ")).thenReturn(1);
        when(consoleReader.readInt("Enter quantity: ")).thenReturn(2);
        when(productManager.getAllProducts()).thenReturn(List.of(product));

        // Act
        shopCli.run();

        // Assert
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().getFirst().getProduct()).isEqualTo(product);
        assertThat(cart.getItems().getFirst().getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should clear cart after placing order")
    public void shouldClearCartAfterPlacingOrder() {
        // Arrange
        cart.addProduct(product, 2);

        when(consoleReader.readInt("Select option: ")).thenReturn(4, 0);
        when(invoiceFileWriter.write(invoice)).thenReturn(INVOICE_PATH);
        when(consoleReader.readLine(PROMOTION)).thenReturn("");

        when(orderProcessor.process(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.markAsProcessing();
                    order.complete();
                    return invoice;
                });

        // Act
        shopCli.run();

        // Assert
        assertThat(cart.isEmpty()).isTrue();
        verify(invoiceFileWriter).write(invoice);
        verify(orderProcessor).process(any(Order.class));
    }

    @Test
    @DisplayName("Should keep cart when order processing fails")
    public void shouldKeepCartWhenOrderProcessingFails() {
        // Arrange
        cart.addProduct(product, 20);

        when(consoleReader.readInt("Select option: ")).thenReturn(4, 0);
        when(orderProcessor.process(any(Order.class))).thenThrow(new InsufficientStockException(PRODUCT_ID));
        when(consoleReader.readLine(PROMOTION)).thenReturn("");

        // Act
        shopCli.run();

        // Assert
        assertThat(cart.isEmpty()).isFalse();
        verifyNoInteractions(invoiceFileWriter);
        verify(orderProcessor).process(any(Order.class));
    }

    @Test
    @DisplayName("Should clear cart when invoice file cannot be written")
    public void shouldClearCartWhenInvoiceFileCannotBeWritten() {
        // Arrange
        cart.addProduct(product, 2);

        when(consoleReader.readInt("Select option: ")).thenReturn(4, 0);
        when(consoleReader.readLine(PROMOTION)).thenReturn("");
        when(orderProcessor.process(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.markAsProcessing();
                    order.complete();
                    return invoice;
                });

        when(invoiceFileWriter.write(invoice))
                .thenThrow(new InvoiceFileException(
                        INVOICE_PATH,
                        new IOException("Test write failure")
                ));

        // Act
        shopCli.run();

        // Assert
        assertThat(cart.isEmpty()).isTrue();
        verify(orderProcessor).process(any(Order.class));
        verify(invoiceFileWriter).write(invoice);
    }

    @Test
    @DisplayName("Should apply promotion when placing order")
    public void shouldApplyPromotionWhenPlacingOrder() {
        // Arrange
        cart.addProduct(product, 2);

        Promotion promotion = new Promotion(
                "SAVE10",
                new BigDecimal("10")
        );

        when(consoleReader.readInt("Select option: ")).thenReturn(4, 0);
        when(consoleReader.readLine(PROMOTION)).thenReturn("SAVE10");
        when(promotionService.findPromotionByCode("SAVE10")).thenReturn(promotion);

        when(orderProcessor.process(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.markAsProcessing();
                    order.complete();
                    return invoice;
                });

        when(invoiceFileWriter.write(invoice)).thenReturn(INVOICE_PATH);

        // Act
        shopCli.run();

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderProcessor).process(orderCaptor.capture());
        Order processedOrder = orderCaptor.getValue();
        assertThat(processedOrder.hasPromotion()).isTrue();
        assertThat(processedOrder.getAppliedPromotion()).isSameAs(promotion);
        assertThat(processedOrder.getDiscountAmount()).isEqualByComparingTo(new BigDecimal("40.00"));
        assertThat(processedOrder.getTotalAmount()).isEqualByComparingTo(new BigDecimal("359.98"));
        verify(promotionService).findPromotionByCode("SAVE10");
    }

    @Test
    @DisplayName("Should keep cart when promotion code does not exist")
    public void shouldKeepCartWhenPromotionCodeDoesNotExist() {
        // Arrange
        cart.addProduct(product, 2);
        when(consoleReader.readInt("Select option: ")).thenReturn(4, 0);
        when(consoleReader.readLine(PROMOTION)).thenReturn("MISSING");

        when(promotionService.findPromotionByCode("MISSING"))
                .thenThrow(new PromotionNotFoundException("MISSING"));

        // Act
        shopCli.run();

        // Assert
        assertThat(cart.isEmpty()).isFalse();
        verify(promotionService).findPromotionByCode("MISSING");
    }
}
