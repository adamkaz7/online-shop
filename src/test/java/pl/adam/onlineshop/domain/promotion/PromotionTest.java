package pl.adam.onlineshop.domain.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PromotionTest {
    private static final String PROMOTION_CODE = "SAVE10";
    private static final BigDecimal DISCOUNT_PERCENTAGE = new BigDecimal("10");

    private static final DiscountPolicy DISCOUNT_POLICY = new PercentageDiscountPolicy(DISCOUNT_PERCENTAGE);

    @Test
    @DisplayName("Should delegate discount calculation to discount policy")
    void shouldDelegateDiscountCalculation() {
        // Arrange
        Promotion promotion = new Promotion(
                PROMOTION_CODE,
                DISCOUNT_POLICY
        );

        BigDecimal amount = new BigDecimal("199.99");

        // Act
        BigDecimal result = promotion.calculateDiscount(amount);

        // Assert
        assertThat(result).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    @DisplayName("Should return discount description")
    void shouldReturnDiscountDescription() {
        // Arrange
        Promotion promotion = new Promotion(
                PROMOTION_CODE,
                DISCOUNT_POLICY
        );

        // Act
        String result = promotion.getDiscountDescription();

        // Assert
        assertThat(result).isEqualTo("10%");
    }

    @Test
    @DisplayName("Should normalize promotion code")
    void shouldNormalizePromotionCode() {
        // Act
        Promotion promotion = new Promotion(
                " save10 ",
                DISCOUNT_POLICY
        );

        // Assert
        assertThat(promotion.getCode()).isEqualTo(PROMOTION_CODE);
    }

    @Test
    @DisplayName("Should reject blank promotion code")
    void shouldRejectBlankPromotionCode() {
        // Act + Assert
        assertThatThrownBy(() -> new Promotion("  ", DISCOUNT_POLICY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Promotion code cannot be blank");
    }
}
