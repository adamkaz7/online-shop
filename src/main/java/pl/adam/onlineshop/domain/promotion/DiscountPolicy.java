package pl.adam.onlineshop.domain.promotion;

import java.math.BigDecimal;

// Design pattern: Strategy
// Defines interchangeable discount calculation algorithms.
public interface DiscountPolicy {
    BigDecimal calculateDiscount(BigDecimal amount);

    String getDescription();
}
