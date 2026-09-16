package pl.adam.onlineshop.exception;

public class PromotionNotFoundException extends RuntimeException {
    public PromotionNotFoundException(String promotionCode) {
        super("Cannot find promotion with code: " + promotionCode);
    }
}
