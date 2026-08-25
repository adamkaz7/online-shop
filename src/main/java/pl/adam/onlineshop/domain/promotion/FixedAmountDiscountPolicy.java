package pl.adam.onlineshop.domain.promotion;

import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FixedAmountDiscountPolicy implements DiscountPolicy {
    @NonNull
    private final BigDecimal discountAmount;

    public FixedAmountDiscountPolicy(@NonNull BigDecimal discountAmount) {
        validateDiscountAmount(discountAmount);
        this.discountAmount = discountAmount;
    }

    @Override
    public BigDecimal calculateDiscount(@NonNull BigDecimal amount) {
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }

        BigDecimal calculatedDiscount = discountAmount;

        if (discountAmount.compareTo(amount) > 0) {
            calculatedDiscount = amount;
        }

        return calculatedDiscount.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescription() {
        return discountAmount + " zł";
    }

    private static void validateDiscountAmount(BigDecimal discountAmount) {
        if (discountAmount.signum() <= 0) {
            throw new IllegalArgumentException("Discount amount must be greater than zero");
        }
    }
}
