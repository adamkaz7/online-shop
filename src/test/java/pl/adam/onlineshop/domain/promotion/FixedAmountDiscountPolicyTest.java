package pl.adam.onlineshop.domain.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FixedAmountDiscountPolicyTest {
    private static final BigDecimal DISCOUNT_AMOUNT =
            new BigDecimal("20");

    private final FixedAmountDiscountPolicy discountPolicy =
            new FixedAmountDiscountPolicy(DISCOUNT_AMOUNT);

    @Test
    @DisplayName("Should calculate fixed amount discount")
    public void shouldCalculateFixedAmountDiscount() {
        // Arrange
        BigDecimal amount = new BigDecimal("199.99");

        // Act
        BigDecimal result = discountPolicy.calculateDiscount(amount);

        // Assert
        assertThat(result).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    @DisplayName("Should limit discount to order amount")
    public void shouldLimitDiscountToOrderAmount() {
        // Arrange
        BigDecimal amount = new BigDecimal("15.00");

        // Act
        BigDecimal result = discountPolicy.calculateDiscount(amount);

        // Assert
        assertThat(result).isEqualByComparingTo(new BigDecimal("15.00"));
    }

    @Test
    @DisplayName("Should return fixed amount description")
    public void shouldReturnFixedAmountDescription() {
        // Act
        String result = discountPolicy.getDescription();

        // Assert
        assertThat(result).isEqualTo("20 zł");
    }

    @Test
    @DisplayName("Should reject non-positive discount amount")
    public void shouldRejectNonPositiveDiscountAmount() {
        // Act + Assert
        assertThatThrownBy(
                () -> new FixedAmountDiscountPolicy(BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Discount amount must be greater than zero"
                );

        assertThatThrownBy(
                () -> new FixedAmountDiscountPolicy(new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Discount amount must be greater than zero"
                );
    }

    @Test
    @DisplayName("Should reject negative order amount")
    public void shouldRejectNegativeOrderAmount() {
        // Act + Assert
        assertThatThrownBy(
                () -> discountPolicy.calculateDiscount(new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount cannot be negative");
    }
}
