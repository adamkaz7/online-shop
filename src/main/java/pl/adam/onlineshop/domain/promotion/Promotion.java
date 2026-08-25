package pl.adam.onlineshop.domain.promotion;

import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Locale;

@Getter
public class Promotion {
    @NonNull
    private final String code;

    @NonNull
    private final DiscountPolicy discountPolicy;

    public Promotion(@NonNull String code, @NonNull DiscountPolicy discountPolicy) {
        validateCode(code);

        this.code = code
                .trim()
                .toUpperCase(Locale.ROOT);
        this.discountPolicy = discountPolicy;
    }

    public BigDecimal calculateDiscount(@NonNull BigDecimal amount) {
        return discountPolicy.calculateDiscount(amount);
    }

    public String getDiscountDescription() {
        return discountPolicy.getDescription();
    }

    private static void validateCode(String code) {
        if (code.isBlank()) {
            throw new IllegalArgumentException("Promotion code cannot be blank");
        }
    }
}
