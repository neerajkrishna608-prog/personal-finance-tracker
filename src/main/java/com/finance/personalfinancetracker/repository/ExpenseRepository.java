package com.finance.personalfinancetracker.repository;

import com.finance.personalfinancetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT COALESCE(SUM(e.amount),0) FROM Expense e WHERE e.user.id = :userId")
    Double getTotalExpense(Long userId);

    List<Expense> findByUserIdOrderByDateDesc(Long userId);
}