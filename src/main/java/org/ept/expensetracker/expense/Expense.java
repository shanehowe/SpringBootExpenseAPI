package org.ept.expensetracker.expense;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Entity
public class Expense {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private Double amount;
    private LocalDate date;
    @Setter
    private Long userId;

    @Setter
    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;

    public Expense(Long id, Double amount, ExpenseCategory category, LocalDate date, Long userId) {
        setId(id);
        setAmount(amount);
        setCategory(category);
        setDate(date);
        setUserId(userId);
    }

    public Expense(ExpenseDto expenseDto, Long userId) {
        this(null, expenseDto.getAmount(), expenseDto.getCategory(), expenseDto.getDate(), userId);
    }

    public Expense() {}

    public void setDate(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        this.date = date;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + getId() +
                ", amount=" + getAmount() +
                ", category=" + getCategory() +
                '}';
    }
}
