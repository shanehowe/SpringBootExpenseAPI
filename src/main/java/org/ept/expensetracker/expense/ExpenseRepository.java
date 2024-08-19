package org.ept.expensetracker.expense;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ExpenseRepository extends
        CrudRepository<Expense, Long>, PagingAndSortingRepository<Expense, Long> {

    Page<Expense> findAllByUserId(Long userId, Pageable pageable);
    Optional<Expense> findByIdAndUserId(Long id, Long userId);
}

