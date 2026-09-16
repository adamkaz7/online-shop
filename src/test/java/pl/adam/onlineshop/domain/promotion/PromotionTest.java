package pl.adam.onlineshop.domain.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PromotionTest {
    private static final String PROMOTION_CODE = "SAVE10";
    private static final BigDecimal DISCOUNT_PERCENTAGE = new BigDecimal("10");

    @Test
    @DisplayName("Should calculate and round discount")
    void shouldCalculateAndRoundDiscount() {
        // Arrange
        Promotion promotion = new Promotion(
                PROMOTION_CODE,
                DISCOUNT_PERCENTAGE
        );

        BigDecimal amount = new BigDecimal("199.99");

        // Act
        BigDecimal result = promotion.calculateDiscount(amount);

        // Assert
        assertThat(result).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    @DisplayName("Should normalize promotion code")
    void shouldNormalizePromotionCode() {
        // Act
        Promotion promotion = new Promotion(
                " save10 ",
                DISCOUNT_PERCENTAGE
        );

        // Assert
        assertThat(promotion.getCode()).isEqualTo(PROMOTION_CODE);
    }

    @Test
    @DisplayName("Should reject blank promotion code")
    void shouldRejectBlankPromotionCode() {
        // Act + Assert
        assertThatThrownBy(() -> new Promotion("  ", DISCOUNT_PERCENTAGE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Promotion code cannot be blank");
    }

    @Test
    @DisplayName("Should reject discount percentage outside allowed range")
    void shouldRejectDiscountPercentageOutsideAllowedRange() {
        // Act + Assert
        assertThatThrownBy(() -> new Promotion(PROMOTION_CODE, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Discount percentage must be greater than zero and not greater than 100");

        assertThatThrownBy(() -> new Promotion(PROMOTION_CODE, new BigDecimal("101")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Discount percentage must be greater than zero and not greater than 100");
    }
}
