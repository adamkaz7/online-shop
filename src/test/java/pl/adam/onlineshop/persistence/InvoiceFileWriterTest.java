package pl.adam.onlineshop.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.OrderItem;
import pl.adam.onlineshop.domain.promotion.Promotion;
import pl.adam.onlineshop.exception.InvoiceFileException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InvoiceFileWriterTest {
    private static final UUID INVOICE_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000001"
    );

    private static final UUID ORDER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000002"
    );

    private static final UUID CUSTOMER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000003"
    );

    private static final UUID PRODUCT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000004"
    );

    private static final UUID SECOND_INVOICE_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000005"
    );

    private static final UUID SECOND_ORDER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000006"
    );

    private static final Instant ISSUED_AT = Instant.parse("2026-07-31T10:00:00Z");

    @TempDir
    Path temporaryDirectory;

    private Path invoiceDirectory;
    private InvoiceFileWriter invoiceFileWriter;
    private Invoice invoice;

    @BeforeEach
    public void setUp() {
        invoiceDirectory = temporaryDirectory.resolve("invoices");
        invoiceFileWriter = new InvoiceFileWriter(invoiceDirectory);
        invoice = createInvoice(
                INVOICE_ID,
                ORDER_ID,
                "Gaming Laptop",
                "199.99",
                2
        );
    }

    @Test
    @DisplayName("Should write invoice to text file")
    public void shouldWriteInvoiceToTextFile() throws IOException {
        // Arrange
        Path expectedPath = invoiceDirectory.resolve("invoice-" + INVOICE_ID + ".txt");

        // Act
        Path result = invoiceFileWriter.write(invoice);

        // Assert
        assertThat(result).isEqualTo(expectedPath);
        assertThat(result).exists().isRegularFile();

        String content = Files.readString(result, StandardCharsets.UTF_8);

        assertThat(content).contains(
                "INVOICE",
                "Invoice ID: " + INVOICE_ID,
                "Order ID: " + ORDER_ID,
                "Customer: Jan Kowalski (ID: " + CUSTOMER_ID + ")",
                "Issued at: 2026-07-31 12:00:00 +02:00 Europe/Warsaw",
                "Gaming Laptop",
                "Quantity: 2",
                "Subtotal: 399.98 zł",
                "Subtotal amount: 399.98 zł",
                "Promotion: none",
                "Discount: 0.00 zł",
                "Total amount: 399.98 zł"
        );

        assertThat(content).containsOnlyOnce("Total amount: 399.98 zł");
    }

    @Test
    @DisplayName("Should overwrite existing invoice file")
    public void shouldOverwriteExistingInvoiceFile() throws IOException {
        // Arrange
        Path originalPath = invoiceFileWriter.write(invoice);

        Invoice updatedInvoice = createInvoice(
                INVOICE_ID,
                ORDER_ID,
                "Laptop",
                "299.99",
                1
        );

        // Act
        Path result = invoiceFileWriter.write(updatedInvoice);

        // Assert
        assertThat(result).isEqualTo(originalPath);

        String content = Files.readString(result, StandardCharsets.UTF_8);

        assertThat(content)
                .contains(
                        "Laptop",
                        "Unit price: 299.99 zł",
                        "Quantity: 1",
                        "Total amount: 299.99 zł"
                )
                .doesNotContain(
                        "Quantity: 2",
                        "399.98 zł"
                );
    }

    @Test
    @DisplayName("Should create separate files for different invoices")
    public void shouldCreateSeparateFilesForDifferentInvoices() throws IOException {
        // Arrange
        Invoice secondInvoice = createInvoice(
                SECOND_INVOICE_ID,
                SECOND_ORDER_ID,
                "Monitor",
                "499.99",
                1
        );

        // Act
        Path firstInvoicePath = invoiceFileWriter.write(invoice);
        Path secondInvoicePath = invoiceFileWriter.write(secondInvoice);

        // Assert
        assertThat(firstInvoicePath).exists().isRegularFile();
        assertThat(secondInvoicePath).exists().isRegularFile();
        assertThat(firstInvoicePath).isNotEqualTo(secondInvoicePath);

        String firstContent = Files.readString(
                firstInvoicePath,
                StandardCharsets.UTF_8
        );

        String secondContent = Files.readString(
                secondInvoicePath,
                StandardCharsets.UTF_8
        );

        assertThat(firstContent).contains(
                "Invoice ID: " + INVOICE_ID,
                "Gaming Laptop"
        );

        assertThat(secondContent).contains(
                "Invoice ID: " + SECOND_INVOICE_ID,
                "Order ID: " + SECOND_ORDER_ID,
                "Monitor",
                "Total amount: 499.99 zł"
        );
    }

    @Test
    @DisplayName("Should throw exception when invoice cannot be written")
    public void shouldThrowExceptionWhenInvoiceCannotBeWritten() throws IOException {
        // Arrange
        Path pathThatIsFile = temporaryDirectory.resolve("blocked");

        Files.writeString(
                pathThatIsFile,
                "This is a file, not a directory",
                StandardCharsets.UTF_8
        );

        InvoiceFileWriter blockedWriter = new InvoiceFileWriter(pathThatIsFile);
        Path expectedInvoicePath = pathThatIsFile.resolve("invoice-" + INVOICE_ID + ".txt");

        // Act + Assert
        assertThatThrownBy(() -> blockedWriter.write(invoice))
                .isInstanceOf(InvoiceFileException.class)
                .hasMessageContaining(expectedInvoicePath.toString())
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("Should write promotion details to invoice file")
    public void shouldWritePromotionDetailsToInvoiceFile() throws IOException {
        // Arrange
        Invoice discountInvoice = createInvoiceWithPromotion(
                INVOICE_ID,
                ORDER_ID,
                "Gaming Laptop",
                "199.99",
                2
        );

        // Act
        Path result = invoiceFileWriter.write(discountInvoice);

        // Assert
        String content = Files.readString(result, StandardCharsets.UTF_8);

        assertThat(content).contains(
                "Subtotal amount: 399.98 zł",
                "Promotion: SAVE10 (10%)",
                "Discount: 40.00 zł",
                "Total amount: 359.98 zł"
        );

        assertThat(content).containsOnlyOnce("Total amount: 359.98 zł");
    }

    private Invoice createInvoice(
            UUID invoiceId,
            UUID orderId,
            String productName,
            String unitPrice,
            int quantity
    ) {
        Customer customer = new Customer(
                CUSTOMER_ID,
                "Jan Kowalski"
        );

        OrderItem item = new OrderItem(
                PRODUCT_ID,
                productName,
                new BigDecimal(unitPrice),
                quantity
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

    private Invoice createInvoiceWithPromotion(
            UUID invoiceId,
            UUID orderId,
            String productName,
            String unitPrice,
            int quantity
    ) {
        Customer customer = new Customer(
                CUSTOMER_ID,
                "Jan Kowalski"
        );

        OrderItem item = new OrderItem(
                PRODUCT_ID,
                productName,
                new BigDecimal(unitPrice),
                quantity
        );

        Promotion promotion = new Promotion(
                "SAVE10",
                new BigDecimal("10")
        );

        BigDecimal subtotalAmount = item.calculateSubtotal();
        BigDecimal discountAmount = promotion.calculateDiscount(subtotalAmount);
        BigDecimal totalAmount = subtotalAmount.subtract(discountAmount);

        return new Invoice(
                invoiceId,
                orderId,
                customer,
                List.of(item),
                subtotalAmount,
                promotion,
                discountAmount,
                totalAmount,
                ISSUED_AT
        );
    }
}
