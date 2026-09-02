package com.finance.personalfinancetracker.repository;

import com.finance.personalfinancetracker.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByUserId(Long userId);

}