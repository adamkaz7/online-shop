package pl.adam.onlineshop.service;

import lombok.NonNull;
import pl.adam.onlineshop.domain.promotion.Promotion;
import pl.adam.onlineshop.exception.PromotionNotFoundException;

import java.util.List;

public class PromotionService {
    private final List<Promotion> promotions;

    public PromotionService(@NonNull List<Promotion> promotions) {
        this.promotions = List.copyOf(promotions);
    }

    public Promotion findPromotionByCode(@NonNull String promotionCode) {
        return promotions.stream()
                .filter(promotion
                        -> promotion.getCode().equalsIgnoreCase(
                        promotionCode.trim()))
                .findFirst()
                .orElseThrow(() -> new PromotionNotFoundException(promotionCode));
    }
}
