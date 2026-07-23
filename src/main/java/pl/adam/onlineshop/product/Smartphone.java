package pl.adam.onlineshop.product;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class Smartphone extends Product {
    private final List<String> availableColors;
    private final List<Integer> availableBatteryCapacity;
    private final List<String> availableAccessories;

    private String selectedColor;
    private int selectedBatteryCapacity;
    private List<String> selectedAccessories;


    public Smartphone(
            String id,
            String name,
            BigDecimal price,
            int availableQuantity,
            List<String> availableColors,
            List<Integer> availableBatteryCapacity,
            List<String> availableAccessories) {
        super(id, name, price, availableQuantity);

        if (availableColors == null || availableColors.isEmpty()) {
            throw new IllegalArgumentException("Available colors cannot be empty");
        }

        if (availableBatteryCapacity == null || availableBatteryCapacity.isEmpty()) {
            throw new IllegalArgumentException("Available battery capacity cannot be empty");
        }

        if (availableAccessories == null || availableAccessories.isEmpty()) {
            throw new IllegalArgumentException("Available accessories cannot be empty");
        }

        this.availableColors = List.copyOf(availableColors);
        this.availableBatteryCapacity = List.copyOf(availableBatteryCapacity);
        this.availableAccessories = List.copyOf(availableAccessories);

        this.selectedColor = availableColors.getFirst();
        this.selectedBatteryCapacity = availableBatteryCapacity.getFirst();
        this.selectedAccessories = List.of();
    }

    public void configure(String color, int batteryCapacity, List<String> accessories) {
        if (!availableColors.contains(color)) {
            throw new IllegalArgumentException("Color " + color + " is not available for this smartphone");
        }

        if (!availableBatteryCapacity.contains(batteryCapacity)) {
            throw new IllegalArgumentException("Battery capacity " + batteryCapacity + " mAh is not available for this smartphone");
        }

        if (accessories == null) {
            throw new IllegalArgumentException("Available accessories cannot be null");
        }

        if (!availableAccessories.containsAll(accessories)) {
            throw new IllegalArgumentException("At least one accessories are not available for this smartphone");
        }

        this.selectedColor = color;
        this.selectedBatteryCapacity = batteryCapacity;
        this.selectedAccessories = List.copyOf(accessories);
    }

    @Override
    public String toString() {
        return super.toString()
                + "\nColor: " + availableColors
                + "\nBattery Capacity: " + availableBatteryCapacity
                + "\nAccessories: " + availableAccessories;
    }
}
