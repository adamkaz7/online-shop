package pl.adam.onlineshop.domain.promotion;

import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

@Getter
public class Promotion {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    @NonNull
    private final String code;

    @NonNull
    private final BigDecimal discountPercentage;

    public Promotion(@NonNull String code, @NonNull BigDecimal discountPercentage) {
        validateCode(code);
        validateDiscountPercentage(discountPercentage);

        this.code = code
                .trim()
                .toUpperCase(Locale.ROOT);
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal calculateDiscount(@NonNull BigDecimal amount) {
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }

        return amount
                .multiply(discountPercentage)
                .divide(ONE_HUNDRED)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static void validateCode(String code) {
        if (code.isBlank()) {
            throw new IllegalArgumentException("Promotion code cannot be blank");
        }
    }

    private static void validateDiscountPercentage(BigDecimal discountPercentage) {
        if (discountPercentage.signum() <= 0 || discountPercentage.compareTo(ONE_HUNDRED) > 0) {
            throw new IllegalArgumentException(
                    "Discount percentage must be greater than zero and not greater than 100"
            );
        }
    }
}
