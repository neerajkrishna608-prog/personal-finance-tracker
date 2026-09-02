package com.finance.personalfinancetracker.model;

import java.time.LocalDate;

public class Transaction {

    private Long id;
    private LocalDate date;
    private String category;
    private Double amount;
    private String type;

    public Transaction() {
    }

    public Transaction(Long id, LocalDate date, String category, Double amount, String type) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}