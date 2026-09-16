package pl.adam.onlineshop.domain.product;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
public class Smartphone extends Product {
    private final List<String> availableColors;
    private final List<Integer> availableBatteryCapacity;
    private final List<String> availableAccessories;

    private String selectedColor;
    private int selectedBatteryCapacity;
    private List<String> selectedAccessories;

    public Smartphone(
            UUID id,
            String name,
            BigDecimal price,
            int availableQuantity,
            List<String> availableColors,
            List<Integer> availableBatteryCapacity,
            List<String> availableAccessories) {
        super(id, name, price, availableQuantity);

        validateAvailableColors(availableColors);
        validateAvailableBatteryCapacity(availableBatteryCapacity);
        validateAvailableAccessories(availableAccessories);

        this.availableColors = List.copyOf(availableColors);
        this.availableBatteryCapacity = List.copyOf(availableBatteryCapacity);
        this.availableAccessories = List.copyOf(availableAccessories);

        this.selectedColor = availableColors.getFirst();
        this.selectedBatteryCapacity = availableBatteryCapacity.getFirst();
        this.selectedAccessories = List.of();
    }

    private static void validateAvailableColors(List<String> availableColors) {
        if (availableColors == null || availableColors.isEmpty()) {
            throw new IllegalArgumentException("Available colors cannot be empty");
        }
    }

    private static void validateAvailableBatteryCapacity(List<Integer> availableBatteryCapacity) {
        if (availableBatteryCapacity == null || availableBatteryCapacity.isEmpty()) {
            throw new IllegalArgumentException("Available battery capacity cannot be empty");
        }
    }

    private static void validateAvailableAccessories(List<String> availableAccessories) {
        if (availableAccessories == null || availableAccessories.isEmpty()) {
            throw new IllegalArgumentException("Available accessories cannot be empty");
        }
    }

    public void configure(String color, int batteryCapacity, List<String> accessories) {
        validateColor(color);
        validateBatteryCapacity(batteryCapacity);
        validateAccessories(accessories);

        this.selectedColor = color;
        this.selectedBatteryCapacity = batteryCapacity;
        this.selectedAccessories = List.copyOf(accessories);
    }

    private void validateColor(String color) {
        if (!availableColors.contains(color)) {
            throw new IllegalArgumentException("Color " + color + " is not available for this smartphone");
        }
    }

    private void validateBatteryCapacity(int batteryCapacity) {
        if (!availableBatteryCapacity.contains(batteryCapacity)) {
            throw new IllegalArgumentException("Battery capacity " + batteryCapacity + " mAh is not available for this smartphone");
        }
    }

    private void validateAccessories(List<String> accessories) {
        if (accessories == null) {
            throw new IllegalArgumentException("Available accessories cannot be null");
        }

        if (!availableAccessories.containsAll(accessories)) {
            throw new IllegalArgumentException("At least one accessories are not available for this smartphone");
        }
    }

    @Override
    public String toString() {
        return super.toString()
                + " | Color: " + availableColors
                + " | Battery Capacity: " + availableBatteryCapacity + " mAh"
                + " | Accessories: " + availableAccessories;
    }
}
