package org.ept.expensetracker.expense;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ExpenseDto {

    private Double amount;
    private ExpenseCategory category;
    private LocalDate date;

    public ExpenseDto(Double amount, ExpenseCategory category, LocalDate date) {
        setAmount(amount);
        setCategory(category);
        setDate(date);
    }

    public ExpenseDto(Double amount, String category) {
        this(amount, ExpenseCategory.fromString(category), LocalDate.now());
    }

    public ExpenseDto() {}

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public void setCategory(String category) {
        setCategory(ExpenseCategory.fromString(category));
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ExpenseDto{" +
                "amount=" + amount +
                ", category=" + category +
                '}';
    }
}
