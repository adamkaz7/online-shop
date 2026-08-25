package pl.adam.onlineshop.domain.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PercentageDiscountPolicyTest {
    private static final BigDecimal DISCOUNT_PERCENTAGE =
            new BigDecimal("10");

    private final PercentageDiscountPolicy discountPolicy =
            new PercentageDiscountPolicy(DISCOUNT_PERCENTAGE);

    @Test
    @DisplayName("Should calculate and round percentage discount")
    void shouldCalculateAndRoundPercentageDiscount() {
        // Arrange
        BigDecimal amount = new BigDecimal("199.99");

        // Act
        BigDecimal result = discountPolicy.calculateDiscount(amount);

        // Assert
        assertThat(result).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    @DisplayName("Should return percentage description")
    void shouldReturnPercentageDescription() {
        // Act
        String result = discountPolicy.getDescription();

        // Assert
        assertThat(result).isEqualTo("10%");
    }

    @Test
    @DisplayName("Should reject percentage outside allowed range")
    void shouldRejectPercentageOutsideAllowedRange() {
        // Act + Assert
        assertThatThrownBy(
                () -> new PercentageDiscountPolicy(BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Discount percentage must be greater than zero "
                                + "and not greater than 100"
                );

        assertThatThrownBy(
                () -> new PercentageDiscountPolicy(new BigDecimal("101")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Discount percentage must be greater than zero "
                                + "and not greater than 100"
                );
    }

    @Test
    @DisplayName("Should reject negative amount")
    void shouldRejectNegativeAmount() {
        // Act + Assert
        assertThatThrownBy(
                () -> discountPolicy.calculateDiscount(new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount cannot be negative");
    }
}
