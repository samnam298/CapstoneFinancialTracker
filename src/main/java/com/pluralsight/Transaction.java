package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;

public class Transaction {
    // ------------------------------------------------------------------
    // Fields (data for each transaction)
    // ------------------------------------------------------------------
    private LocalDate date;
    private LocalTime time;
    private String description;
    private String vendor;
    private double amount;

    // ------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------
    public Transaction(LocalDate date, LocalTime time, String description, String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public String getDescription() { return description; }
    public String getVendor() { return vendor; }
    public double getAmount() { return amount; }

    // ------------------------------------------------------------------
    // For easy printing in the console
    // ------------------------------------------------------------------
    @Override
    public String toString() {
        return String.format("%s | %s | %-20s | %-10s | %10.2f",
                date, time, description, vendor, amount);
    }
}

