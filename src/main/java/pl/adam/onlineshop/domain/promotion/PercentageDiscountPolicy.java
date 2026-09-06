package pl.adam.onlineshop.domain.promotion;

import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PercentageDiscountPolicy implements DiscountPolicy {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final BigDecimal discountPercentage;

    public PercentageDiscountPolicy(@NonNull BigDecimal discountPercentage) {
        validateDiscountPercentage(discountPercentage);
        this.discountPercentage = discountPercentage;
    }

    @Override
    public BigDecimal calculateDiscount(@NonNull BigDecimal amount) {
        validateAmount(amount);

        return amount
                .multiply(discountPercentage)
                .divide(ONE_HUNDRED)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescription() {
        return discountPercentage + "%";
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }

    private static void validateDiscountPercentage(BigDecimal discountPercentage) {
        if (discountPercentage.signum() <= 0 || discountPercentage.compareTo(ONE_HUNDRED) > 0) {
            throw new IllegalArgumentException(
                    "Discount percentage must be greater than zero "
                            + "and not greater than 100"
            );
        }
    }
}
