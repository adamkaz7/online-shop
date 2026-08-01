package pl.adam.onlineshop.persistence;

import lombok.NonNull;
import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.OrderItem;
import pl.adam.onlineshop.exception.InvoiceFileException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;

public class InvoiceFileWriter {
    private static final String FILE_PREFIX = "invoice-";
    private static final String FILE_EXTENSION = ".txt";

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path invoiceDirectory;

    public InvoiceFileWriter(@NonNull Path invoiceDirectory) {
        this.invoiceDirectory = invoiceDirectory;
    }

    public Path write(@NonNull Invoice invoice) {
        Path invoicePath = createInvoicePath(invoice);

        try {
            Files.createDirectories(invoiceDirectory);

            try (BufferedWriter writer = Files.newBufferedWriter(
                    invoicePath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            )) {
                writeInvoice(writer, invoice);
            }

            return invoicePath;
        } catch (IOException exception) {
            throw new InvoiceFileException(invoicePath, exception);
        }
    }

    private Path createInvoicePath(@NonNull Invoice invoice) {
        String fileName = FILE_PREFIX + invoice.getInvoiceId() + FILE_EXTENSION;

        return invoiceDirectory.resolve(fileName);
    }

    private void writeInvoice(
            BufferedWriter writer,
            @NonNull Invoice invoice
    ) throws IOException {
        writer.write("INVOICE");
        writer.newLine();
        writer.newLine();

        writer.write("Invoice ID: " + invoice.getInvoiceId());
        writer.newLine();

        writer.write("Order ID: " + invoice.getOrderId());
        writer.newLine();

        writer.write("Customer: " + invoice.getCustomer());
        writer.newLine();

        writer.write("Issued at: " + invoice.getIssuedAt().format(DATE_TIME_FORMATTER));
        writer.newLine();

        writer.write("Items:");
        writer.newLine();

        for (OrderItem item : invoice.getItems()) {
            writer.write("- " + item);
            writer.newLine();
        }
        writer.newLine();

        writer.write("Subtotal amount: " + invoice.getSubtotalAmount() + " zł");
        writer.newLine();

        String promotionDescription = invoice.hasPromotion()
                ? invoice.getPromotion().getCode()
                  + " ("
                  + invoice.getPromotion().getDiscountPercentage()
                  + "%)"
                : "none";

        writer.write("Promotion: " + promotionDescription);
        writer.newLine();

        writer.write("Discount: " + invoice.getDiscountAmount() + " zł");
        writer.newLine();

        writer.write("Total amount: " + invoice.getTotalAmount() + " zł");
        writer.newLine();
    }
}
