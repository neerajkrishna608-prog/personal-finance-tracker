package com.finance.personalfinancetracker;

import com.finance.personalfinancetracker.model.Budget;
import com.finance.personalfinancetracker.model.User;
import com.finance.personalfinancetracker.repository.BudgetRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    @PostMapping("/budget/save")
    public String saveBudget(
            @RequestParam Double monthlyBudget,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Budget budget = budgetRepository
                .findByUserId(user.getId())
                .orElse(new Budget());

        budget.setMonthlyBudget(monthlyBudget);

        budget.setUser(user);

        budgetRepository.save(budget);

        return "redirect:/dashboard";
    }
}