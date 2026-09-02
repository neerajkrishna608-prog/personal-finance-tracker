package com.finance.personalfinancetracker;

import com.finance.personalfinancetracker.model.Income;
import com.finance.personalfinancetracker.model.User;
import com.finance.personalfinancetracker.repository.IncomeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class IncomeController {

    @Autowired
    private IncomeRepository incomeRepository;

    @GetMapping("/income")
public String showIncomePage(
        HttpSession session,
        Model model) {

    User user = (User) session.getAttribute("loggedInUser");

    if (user == null) {
        return "redirect:/login";
    }

    model.addAttribute("incomes", incomeRepository.findByUserIdOrderByDateDesc(user.getId()));

    return "income";
}

    @GetMapping("/income/edit/{id}")
    public String editIncome(
            @PathVariable Long id,
            Model model,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Income income = incomeRepository.findById(id).orElse(null);

        if (income == null ||
                income.getUser() == null ||
                !income.getUser().getId().equals(user.getId())) {

            return "redirect:/dashboard";
        }

        model.addAttribute("income", income);

        return "income";
    }

    @PostMapping("/income/save")
    public String saveIncome(
            @RequestParam(required = false) Long id,
            @RequestParam String source,
            @RequestParam Double amount,
            @RequestParam String date,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Income income;

        if (id != null) {

            income = incomeRepository.findById(id).orElse(new Income());

            if (income.getUser() != null &&
                    !income.getUser().getId().equals(user.getId())) {

                return "redirect:/dashboard";
            }

            income.setId(id);

        } else {
            income = new Income();
        }

        income.setSource(source);
        income.setAmount(amount);
        income.setDate(LocalDate.parse(date));

        // Connect this income to the logged-in user
        income.setUser(user);

        incomeRepository.save(income);

        return "redirect:/dashboard";
    }

    @GetMapping("/income/delete/{id}")
    public String deleteIncome(
            @PathVariable Long id,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Income income = incomeRepository.findById(id).orElse(null);

        if (income != null &&
                income.getUser() != null &&
                income.getUser().getId().equals(user.getId())) {

            incomeRepository.delete(income);
        }

        return "redirect:/dashboard";
    }
}