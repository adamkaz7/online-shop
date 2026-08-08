package pl.adam.onlineshop.cli;

import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class ConsoleReader {
    private final Scanner scanner = new Scanner(System.in);

    public String readLine(String message) {
        log.info("{}", message);
        return scanner.nextLine().trim();
    }

    public int readInt(String message) {
        while (true) {
            String input = readLine(message);

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ignored) {
                log.warn("Please enter a valid number.");
            }
        }
    }
}
