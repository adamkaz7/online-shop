package pl.adam.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.adam.onlineshop.domain.promotion.Promotion;
import pl.adam.onlineshop.exception.PromotionNotFoundException;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PromotionServiceTest {
    private static final String PROMOTION_CODE = "SAVE10";

    private Promotion promotion;
    private PromotionService promotionService;

    @BeforeEach
    public void setUp() {
        promotion = new Promotion(
                PROMOTION_CODE,
                new BigDecimal("10")
        );

        promotionService = new PromotionService(
                List.of(promotion)
        );
    }

    @Test
    @DisplayName("Should find promotion ignoring case and spaces")
    public void shouldFindPromotionIgnoringCaseAndSpaces() {
        // Act
        Promotion result = promotionService.findPromotionByCode(" save10 ");

        // Assert
        assertThat(result).isSameAs(promotion);
    }

    @Test
    @DisplayName("Should throw exception when promotion does not exist")
    public void shouldThrowExceptionWhenPromotionDoesNotExist() {
        // Act + Assert
        assertThatThrownBy(() -> promotionService.findPromotionByCode("Missing"))
                .isInstanceOf(PromotionNotFoundException.class)
                .hasMessage("Cannot find promotion with code: Missing");
    }
}
