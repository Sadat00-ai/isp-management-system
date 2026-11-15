package isp;

import java.io.Console;
import java.util.Scanner;

public class InputUtil {
    private final Scanner scanner;
    public InputUtil(Scanner scanner) { this.scanner = scanner; }

    private String nextLine() {
        if (!scanner.hasNextLine()) {
            // input was closed (Ctrl+D / Ctrl+Z), data is already saved after every action
            Ui.println();
            Ui.info("Input closed. Exiting.");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    public String readText(String prompt) {
        Ui.out.print(Ui.prompt(prompt));
        Ui.out.flush();
        String line = nextLine();
        if (Ui.echoInput) Ui.echo(line);
        return line;
    }

    // hides the typed password when a real terminal is available
    public String readPassword(String prompt) {
        Console console = System.console();
        if (console != null && !Ui.echoInput) {
            char[] typed = console.readPassword("%s", Ui.prompt(prompt));
            if (typed != null) return new String(typed).trim();
        }
        Ui.out.print(Ui.prompt(prompt));
        Ui.out.flush();
        String line = nextLine();
        if (Ui.echoInput) Ui.echo(Ui.rep("*", line.length()));
        return line;
    }

    public int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readText(prompt));
            } catch (NumberFormatException e) {
                Ui.err("Please enter a valid whole number.");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            try {
                double value = Double.parseDouble(readText(prompt));
                if (value < 0) throw new NumberFormatException();
                return value;
            } catch (NumberFormatException e) {
                Ui.err("Please enter a valid non-negative amount.");
            }
        }
    }
}
