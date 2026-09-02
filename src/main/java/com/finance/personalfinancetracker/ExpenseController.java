package com.finance.personalfinancetracker;

import com.finance.personalfinancetracker.model.Expense;
import com.finance.personalfinancetracker.model.User;
import com.finance.personalfinancetracker.repository.ExpenseRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping("/expense")
public String showExpensePage(
        HttpSession session,
        Model model) {

    User user = (User) session.getAttribute("loggedInUser");

    if (user == null) {
        return "redirect:/login";
    }

    model.addAttribute(
            "expenses",
            expenseRepository.findByUserIdOrderByDateDesc(user.getId())
    );

    return "expense";
}

    @GetMapping("/expense/edit/{id}")
    public String editExpense(
            @PathVariable Long id,
            Model model,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Expense expense = expenseRepository.findById(id).orElse(null);

        if (expense == null ||
                expense.getUser() == null ||
                !expense.getUser().getId().equals(user.getId())) {

            return "redirect:/dashboard";
        }

        model.addAttribute("expense", expense);

        return "expense";
    }

    @PostMapping("/expense/save")
    public String saveExpense(
            @RequestParam(required = false) Long id,
            @RequestParam String category,
            @RequestParam Double amount,
            @RequestParam String date,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Expense expense;

        if (id != null) {

            expense = expenseRepository.findById(id).orElse(new Expense());

            if (expense.getUser() != null &&
                    !expense.getUser().getId().equals(user.getId())) {

                return "redirect:/dashboard";
            }

            expense.setId(id);

        } else {
            expense = new Expense();
        }

        expense.setCategory(category);
        expense.setAmount(amount);
        expense.setDate(LocalDate.parse(date));

        // Connect this expense to the logged-in user
        expense.setUser(user);

        expenseRepository.save(expense);

        return "redirect:/dashboard";
    }

    @GetMapping("/expense/delete/{id}")
    public String deleteExpense(
            @PathVariable Long id,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Expense expense = expenseRepository.findById(id).orElse(null);

        if (expense != null &&
                expense.getUser() != null &&
                expense.getUser().getId().equals(user.getId())) {

            expenseRepository.delete(expense);
        }

        return "redirect:/dashboard";
    }
}