package pl.adam.onlineshop.persistence;

import lombok.NonNull;
import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.OrderItem;
import pl.adam.onlineshop.domain.promotion.Promotion;
import pl.adam.onlineshop.exception.InvoiceFileException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class InvoiceFileWriter {
    private static final String FILE_PREFIX = "invoice-";
    private static final String FILE_EXTENSION = ".txt";

    private static final ZoneId SHOP_ZONE = ZoneId.of("Europe/Warsaw");

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter
                    .ofPattern("yyyy-MM-dd HH:mm:ss XXX VV")
                    .withZone(SHOP_ZONE);

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
        writeLine(writer, "INVOICE");
        writeLine(writer, "");

        writeLine(writer, "Invoice ID: " + invoice.getInvoiceId());
        writeLine(writer, "Order ID: " + invoice.getOrderId());
        writeLine(writer, "Customer: " + invoice.getCustomer());
        writeLine(writer, "Issued at: " + DATE_TIME_FORMATTER.format(invoice.getIssuedAt()));
        writeLine(writer, "Items:");

        for (OrderItem item : invoice.getItems()) {
            writeLine(writer, "- " + item);
        }

        writeLine(writer, "");
        writeLine(writer, "Subtotal amount: " + invoice.getSubtotalAmount() + " zł");

        String promotionDescription = createPromotionDescription(invoice);

        writeLine(writer, "Promotion: " + promotionDescription);
        writeLine(writer, "Discount: " + invoice.getDiscountAmount() + " zł");
        writeLine(writer, "Total amount: " + invoice.getTotalAmount() + " zł");
    }

    private String createPromotionDescription(Invoice invoice) {
        if (!invoice.hasPromotion()) {
            return "none";
        }

        Promotion promotion = invoice.getPromotion();

        return promotion.getCode()
                + " ("
                + promotion.getDiscountDescription()
                + ")";
    }

    private void writeLine(
            BufferedWriter writer,
            String content
    ) throws IOException {
        writer.write(content);
        writer.newLine();
    }
}
