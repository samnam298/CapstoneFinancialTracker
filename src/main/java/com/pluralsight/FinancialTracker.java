package com.pluralsight;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class FinancialTracker {

    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    public static void main(String[] args) {
        loadTransactions(FILE_NAME);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }

    /* ------------------------------------------------------------------
       File I/O
       ------------------------------------------------------------------ */
    public static void loadTransactions(String fileName) {
        File file = new File(fileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println("Error creating transactions file: " + e.getMessage());
            }
            return;
        }

        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length != 5) continue;

                LocalDate date = LocalDate.parse(parts[0], DATE_FMT);
                LocalTime time = LocalTime.parse(parts[1], TIME_FMT);
                String description = parts[2];
                String vendor = parts[3];
                double amount = Double.parseDouble(parts[4]);

                transactions.add(new Transaction(date, time, description, vendor, amount));
            }
        } catch (Exception e) {
            System.out.println("Error reading transactions: " + e.getMessage());
        }
    }

    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */
    private static void addDeposit(Scanner scanner) {
        try {
            System.out.print("Enter date (yyyy-MM-dd): ");
            LocalDate date = parseDate(scanner.nextLine());
            if (date == null) return;

            System.out.print("Enter time (HH:mm:ss): ");
            LocalTime time = LocalTime.parse(scanner.nextLine(), TIME_FMT);

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            System.out.print("Enter vendor: ");
            String vendor = scanner.nextLine();

            System.out.print("Enter amount: ");
            Double amount = parseDouble(scanner.nextLine());
            if (amount == null || amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }

            Transaction t = new Transaction(date, time, description, vendor, amount);
            transactions.add(t);

            try (java.io.BufferedWriter writer =
                         new java.io.BufferedWriter(new java.io.FileWriter(FILE_NAME, true))) {
                String line = String.format("%s|%s|%s|%s|%.2f",
                        date.format(DATE_FMT), time.format(TIME_FMT), description, vendor, amount);
                writer.newLine();
                writer.write(line);
            }

            System.out.println("✅ Deposit added successfully.");
        } catch (Exception e) {
            System.out.println("⚠️ Error adding deposit: " + e.getMessage());
        }
    }

    private static void addPayment(Scanner scanner) {
        try {
            System.out.print("Enter date (yyyy-MM-dd): ");
            LocalDate date = parseDate(scanner.nextLine());
            if (date == null) return;

            System.out.print("Enter time (HH:mm:ss): ");
            LocalTime time = LocalTime.parse(scanner.nextLine(), TIME_FMT);

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            System.out.print("Enter vendor: ");
            String vendor = scanner.nextLine();

            System.out.print("Enter amount: ");
            Double amount = parseDouble(scanner.nextLine());
            if (amount == null || amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }

            amount = -amount;
            Transaction t = new Transaction(date, time, description, vendor, amount);
            transactions.add(t);

            try (java.io.BufferedWriter writer =
                         new java.io.BufferedWriter(new java.io.FileWriter(FILE_NAME, true))) {
                String line = String.format("%s|%s|%s|%s|%.2f",
                        date.format(DATE_FMT), time.format(TIME_FMT), description, vendor, amount);
                writer.newLine();
                writer.write(line);
            }

            System.out.println("✅ Payment added successfully.");
        } catch (Exception e) {
            System.out.println("⚠️ Error adding payment: " + e.getMessage());
        }
    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Display helpers
       ------------------------------------------------------------------ */
    private static void displayLedger() {
        System.out.printf("%-12s %-10s %-25s %-20s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("----------------------------------------------------------------------");
        for (int i = transactions.size() - 1; i >= 0; i--) {
            Transaction t = transactions.get(i);
            System.out.printf("%-12s %-10s %-25s %-20s %10.2f%n",
                    t.getDate().format(DATE_FMT),
                    t.getTime().format(TIME_FMT),
                    t.getDescription(),
                    t.getVendor(),
                    t.getAmount());
        }
    }

    private static void displayDeposits() {
        System.out.printf("%-12s %-10s %-25s %-20s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("----------------------------------------------------------------------");
        for (int i = transactions.size() - 1; i >= 0; i--) {
            Transaction t = transactions.get(i);
            if (t.getAmount() > 0) {
                System.out.printf("%-12s %-10s %-25s %-20s %10.2f%n",
                        t.getDate().format(DATE_FMT),
                        t.getTime().format(TIME_FMT),
                        t.getDescription(),
                        t.getVendor(),
                        t.getAmount());
            }
        }
    }

    private static void displayPayments() {
        System.out.printf("%-12s %-10s %-25s %-20s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("----------------------------------------------------------------------");
        for (int i = transactions.size() - 1; i >= 0; i--) {
            Transaction t = transactions.get(i);
            if (t.getAmount() < 0) {
                System.out.printf("%-12s %-10s %-25s %-20s %10.2f%n",
                        t.getDate().format(DATE_FMT),
                        t.getTime().format(TIME_FMT),
                        t.getDescription(),
                        t.getVendor(),
                        t.getAmount());
            }
        }
    }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {
                    LocalDate now = LocalDate.now();
                    LocalDate start = now.withDayOfMonth(1);
                    filterTransactionsByDate(start, now);
                }
                case "2" -> {
                    LocalDate now = LocalDate.now();
                    LocalDate firstOfPrev = now.minusMonths(1).withDayOfMonth(1);
                    LocalDate endOfPrev = firstOfPrev.plusMonths(1).minusDays(1);
                    filterTransactionsByDate(firstOfPrev, endOfPrev);
                }
                case "3" -> {
                    LocalDate now = LocalDate.now();
                    LocalDate start = LocalDate.of(now.getYear(), 1, 1);
                    filterTransactionsByDate(start, now);
                }
                case "4" -> {
                    LocalDate now = LocalDate.now();
                    LocalDate start = LocalDate.of(now.getYear() - 1, 1, 1);
                    LocalDate end = LocalDate.of(now.getYear() - 1, 12, 31);
                    filterTransactionsByDate(start, end);
                }
                case "5" -> {
                    System.out.print("Enter vendor name: ");
                    String vendor = scanner.nextLine();
                    filterTransactionsByVendor(vendor);
                }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        System.out.printf("%-12s %-10s %-25s %-20s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("----------------------------------------------------------------------");
        for (Transaction t : transactions) {
            if ((t.getDate().isEqual(start) || t.getDate().isAfter(start)) &&
                    (t.getDate().isEqual(end) || t.getDate().isBefore(end))) {
                System.out.printf("%-12s %-10s %-25s %-20s %10.2f%n",
                        t.getDate().format(DATE_FMT),
                        t.getTime().format(TIME_FMT),
                        t.getDescription(),
                        t.getVendor(),
                        t.getAmount());
            }
        }
    }

    private static void filterTransactionsByVendor(String vendor) {
        System.out.printf("%-12s %-10s %-25s %-20s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("----------------------------------------------------------------------");
        for (Transaction t : transactions) {
            if (t.getVendor().equalsIgnoreCase(vendor.trim())) {
                System.out.printf("%-12s %-10s %-25s %-20s %10.2f%n",
                        t.getDate().format(DATE_FMT),
                        t.getTime().format(TIME_FMT),
                        t.getDescription(),
                        t.getVendor(),
                        t.getAmount());
            }
        }
    }

    private static void customSearch(Scanner scanner) {
        System.out.println("Custom Search — leave blank to skip a field");
        System.out.print("Start date (yyyy-MM-dd): ");
        String startInput = scanner.nextLine().trim();
        System.out.print("End date (yyyy-MM-dd): ");
        String endInput = scanner.nextLine().trim();
        System.out.print("Description: ");
        String descInput = scanner.nextLine().trim();
        System.out.print("Vendor: ");
        String vendorInput = scanner.nextLine().trim();
        System.out.print("Exact amount: ");
        String amountInput = scanner.nextLine().trim();

        LocalDate start = startInput.isEmpty() ? null : LocalDate.parse(startInput, DATE_FMT);
        LocalDate end = endInput.isEmpty() ? null : LocalDate.parse(endInput, DATE_FMT);
        Double amount = amountInput.isEmpty() ? null : Double.parseDouble(amountInput);

        System.out.printf("%-12s %-10s %-25s %-20s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("----------------------------------------------------------------------");

        for (Transaction t : transactions) {
            boolean match = true;
            if (start != null && t.getDate().isBefore(start)) match = false;
            if (end != null && t.getDate().isAfter(end)) match = false;
            if (!descInput.isEmpty() && !t.getDescription().toLowerCase().contains(descInput.toLowerCase()))
                match = false;
            if (!vendorInput.isEmpty() && !t.getVendor().toLowerCase().contains(vendorInput.toLowerCase()))
                match = false;
            if (amount != null && t.getAmount() != amount) match = false;

            if (match) {
                System.out.printf("%-12s %-10s %-25s %-20s %10.2f%n",
                        t.getDate().format(DATE_FMT),
                        t.getTime().format(TIME_FMT),
                        t.getDescription(),
                        t.getVendor(),
                        t.getAmount());
            }
        }
    }

    /* ------------------------------------------------------------------
       Utility parsers
       ------------------------------------------------------------------ */
    private static LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s, DATE_FMT);
        } catch (Exception e) {
            System.out.println("⚠️ Invalid date format. Please use yyyy-MM-dd.");
            return null;
        }
    }

    private static Double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            System.out.println("⚠️ Invalid number format. Please enter a valid amount.");
            return null;
        }
    }
}
