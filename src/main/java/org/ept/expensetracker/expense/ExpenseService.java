package org.ept.expensetracker.expense;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Page<Expense> findAll(Pageable pageable, Long userId) {
        return expenseRepository.findAllByUserId(
                userId,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                )
        );
    }

    public Expense findById(Long id, Long userId) {
        return expenseRepository.findByIdAndUserId(id, userId).orElse(null);
    }

    public Expense createExpense(ExpenseDto expenseDto, Long userId) {
        if (expenseDto.getAmount() <= 0 || expenseDto.getCategory() == null) {
            throw new IllegalArgumentException("Invalid expense");
        }
        return expenseRepository.save(new Expense(expenseDto, userId));
    }

    public void updateExpense(Long id, ExpenseDto expenseDto, Long userId) {
        Expense expense = expenseRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found"));
        if (expenseDto.getAmount() <= 0 || expenseDto.getCategory() == null) {
            throw new IllegalArgumentException("Invalid expense");
        }
        expense.setAmount(expenseDto.getAmount());
        expense.setCategory(expenseDto.getCategory());
        expenseRepository.save(expense);
    }
}
