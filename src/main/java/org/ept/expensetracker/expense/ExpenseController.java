package org.ept.expensetracker.expense;

import org.ept.expensetracker.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/{id}")
    private ResponseEntity<Expense> findById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Optional<Expense> expense = Optional.ofNullable(expenseService.findByIdAndUserId(id, user.getId()));
        return expense.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    private ResponseEntity<Iterable<Expense>> findAll(@PageableDefault(size = 100) Pageable pageable,
                                                      @AuthenticationPrincipal User user) {
        Page<Expense> expenses = expenseService.findAll(pageable, user.getId());
        return ResponseEntity.ok(expenses.getContent());
    }

    @PostMapping
    private ResponseEntity<?> createExpense(@RequestBody ExpenseDto expenseDto, @AuthenticationPrincipal User user) {
        try {
            Expense expense = expenseService.createExpense(expenseDto, user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(expense);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    private ResponseEntity<?> updateExpense(@PathVariable Long id,
                                            @RequestBody ExpenseDto expenseDto,
                                            @AuthenticationPrincipal User user) {
        try {
            expenseService.updateExpense(id, expenseDto, user.getId());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
