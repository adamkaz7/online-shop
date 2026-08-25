package pl.adam.onlineshop.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.OrderItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryInvoiceRepositoryTest {
    private static final UUID INVOICE_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000001"
    );

    private static final UUID SECOND_INVOICE_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000002"
    );

    private static final UUID ORDER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000003"
    );

    private static final UUID SECOND_ORDER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000004"
    );

    private static final UUID CUSTOMER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000005"
    );

    private static final UUID PRODUCT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000006"
    );

    private static final UUID MISSING_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000999"
    );

    private static final Instant ISSUED_AT = Instant.parse("2026-08-02T10:00:00Z");

    private InMemoryInvoiceRepository invoiceRepository;
    private Invoice invoice;

    @BeforeEach
    public void setUp() {
        invoiceRepository = new InMemoryInvoiceRepository();
        invoice = createInvoice(INVOICE_ID, ORDER_ID);
    }

    private Invoice createInvoice(UUID invoiceId, UUID orderId) {
        Customer customer = new Customer(
                CUSTOMER_ID,
                "Jan Kowalski"
        );

        OrderItem item = new OrderItem(
                PRODUCT_ID,
                "Gaming Laptop",
                new BigDecimal("199.99"),
                1
        );

        return new Invoice(
                invoiceId,
                orderId,
                customer,
                List.of(item),
                item.calculateSubtotal(),
                ISSUED_AT
        );
    }

    @Test
    @DisplayName("Should save and find invoice by ID")
    void shouldSaveAndFindInvoiceById() {
        // Arrange
        invoiceRepository.save(invoice);

        // Act
        Optional<Invoice> result = invoiceRepository.findById(INVOICE_ID);

        // Assert
        assertThat(result)
                .isPresent()
                .contains(invoice);
    }

    @Test
    @DisplayName("Should find invoice by order ID")
    void shouldFindInvoiceByOrderId() {
        // Arrange
        invoiceRepository.save(invoice);

        // Act
        Optional<Invoice> result = invoiceRepository.findByOrderId(ORDER_ID);

        // Assert
        assertThat(result)
                .isPresent()
                .contains(invoice);
    }

    @Test
    @DisplayName("Should return empty when invoice does not exist")
    void shouldReturnEmptyWhenInvoiceDoesNotExist() {
        // Act
        Optional<Invoice> result = invoiceRepository.findById(MISSING_ID);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return all invoices")
    void shouldReturnAllInvoices() {
        // Arrange
        Invoice secondInvoice = createInvoice(SECOND_INVOICE_ID, SECOND_ORDER_ID);
        invoiceRepository.save(invoice);
        invoiceRepository.save(secondInvoice);

        // Act
        List<Invoice> result = invoiceRepository.findAll();

        // Assert
        assertThat(result)
                .containsExactlyInAnyOrder(invoice, secondInvoice);
    }
}
