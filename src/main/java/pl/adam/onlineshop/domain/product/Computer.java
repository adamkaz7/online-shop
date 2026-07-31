package pl.adam.onlineshop.domain.product;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
public class Computer extends Product {
    private final List<String> availableProcessors;
    private final List<Integer> availableRam;

    private String selectedProcessor;
    private int selectedRam;

    public Computer(
            UUID id,
            String name,
            BigDecimal price,
            int availableQuantity,
            List<String> availableProcessors,
            List<Integer> availableRam) {
        super(id, name, price, availableQuantity);

        if (availableProcessors == null || availableProcessors.isEmpty()) {
            throw new IllegalArgumentException("Available processors cannot be empty");
        }

        if (availableRam == null || availableRam.isEmpty()) {
            throw new IllegalArgumentException("Available RAM cannot be empty");
        }

        this.availableProcessors = List.copyOf(availableProcessors);
        this.availableRam = List.copyOf(availableRam);
        this.selectedProcessor = availableProcessors.getFirst();
        this.selectedRam = availableRam.getFirst();
    }

    public void configure(String processor, int ram) {
        if (!availableProcessors.contains(processor)) {
            throw new IllegalArgumentException("Processor " + processor + " is not available for this computer");
        }

        if (!availableRam.contains(ram)) {
            throw new IllegalArgumentException("RAM option " + ram + " GB is not available for this computer");
        }

        this.selectedProcessor = processor;
        this.selectedRam = ram;
    }

    @Override
    public String toString() {
        return super.toString()
                + " | Processor: " + selectedProcessor
                + " | RAM: " + selectedRam + " GB";
    }
}