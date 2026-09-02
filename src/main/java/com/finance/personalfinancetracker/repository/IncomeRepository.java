package com.finance.personalfinancetracker.repository;

import com.finance.personalfinancetracker.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    @Query("SELECT COALESCE(SUM(i.amount),0) FROM Income i WHERE i.user.id = :userId")
    Double getTotalIncome(Long userId);

    List<Income> findByUserIdOrderByDateDesc(Long userId);
}