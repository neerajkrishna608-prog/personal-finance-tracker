package com.finance.personalfinancetracker;
import com.finance.personalfinancetracker.model.User;
import jakarta.servlet.http.HttpSession;
import com.finance.personalfinancetracker.model.Budget;
import com.finance.personalfinancetracker.repository.BudgetRepository;
import com.finance.personalfinancetracker.model.Expense;
import com.finance.personalfinancetracker.model.Income;
import com.finance.personalfinancetracker.model.Transaction;
import com.finance.personalfinancetracker.repository.ExpenseRepository;
import com.finance.personalfinancetracker.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Controller
public class HomeController {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
private BudgetRepository budgetRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Personal Finance Tracker");
        return "index";
    }

    // ================= DASHBOARD =================

    @GetMapping("/dashboard")
public String dashboard(Model model, HttpSession session) {

    User user = (User) session.getAttribute("loggedInUser");

    if (user == null) {
        return "redirect:/login";
    }

    loadDashboardData(model, user);

    List<Income> incomes =
            incomeRepository.findByUserIdOrderByDateDesc(user.getId());

    List<Expense> expenses =
            expenseRepository.findByUserIdOrderByDateDesc(user.getId());


        double food = 0;
double shopping = 0;
double bills = 0;
double transport = 0;
double other = 0;

for (Expense e : expenses) {

    if (e.getCategory() == null) continue;

    switch (e.getCategory().toLowerCase()) {

        case "food":
            food += e.getAmount();
            break;

        case "shopping":
            shopping += e.getAmount();
            break;

        case "bills":
            bills += e.getAmount();
            break;

        case "transport":
            transport += e.getAmount();
            break;

        default:
            other += e.getAmount();
    }
}

model.addAttribute("expenseBreakdown",
        List.of(food, shopping, bills, transport, other));

        List<Transaction> transactions = new ArrayList<>();

        for (Income income : incomes) {

            transactions.add(
                    new Transaction(
                            income.getId(),
                            income.getDate(),
                            income.getSource(),
                            income.getAmount(),
                            "Income"
                    )
            );

        }

        for (Expense expense : expenses) {

            transactions.add(
                    new Transaction(
                            expense.getId(),
                            expense.getDate(),
                            expense.getCategory(),
                            expense.getAmount(),
                            "Expense"
                    )
            );

        }

        transactions.sort(
                Comparator.comparing(Transaction::getDate).reversed()
        );

        model.addAttribute("transactions", transactions);

        return "dashboard";
    }

    // ================= REPORTS =================

    @GetMapping("/reports")
public String reports(Model model, HttpSession session) {

    User user = (User) session.getAttribute("loggedInUser");

    if (user == null) {
        return "redirect:/login";
    }

    loadDashboardData(model, user);

    return "reports";
}

    // ================= TRANSACTIONS =================

    @GetMapping("/transactions")
public String transactions(Model model, HttpSession session) {

    User user = (User) session.getAttribute("loggedInUser");

    if (user == null) {
        return "redirect:/login";
    }

    List<Income> incomes =
            incomeRepository.findByUserIdOrderByDateDesc(user.getId());

    List<Expense> expenses =
            expenseRepository.findByUserIdOrderByDateDesc(user.getId());

        List<Transaction> transactions = new ArrayList<>();

        for (Income income : incomes) {

            transactions.add(
                    new Transaction(
                            income.getId(),
                            income.getDate(),
                            income.getSource(),
                            income.getAmount(),
                            "Income"
                    )
            );

        }

        for (Expense expense : expenses) {

            transactions.add(
                    new Transaction(
                            expense.getId(),
                            expense.getDate(),
                            expense.getCategory(),
                            expense.getAmount(),
                            "Expense"
                    )
            );

        }

        transactions.sort(
                Comparator.comparing(Transaction::getDate).reversed()
        );

        model.addAttribute("transactions", transactions);

        return "transactions";
    }

    // ================= COMMON DASHBOARD DATA =================

    private void loadDashboardData(Model model, User user) {

        Double totalIncome = incomeRepository.getTotalIncome(user.getId());
Double totalExpense = expenseRepository.getTotalExpense(user.getId());

        if (totalIncome == null)
            totalIncome = 0.0;

        if (totalExpense == null)
            totalExpense = 0.0;

        Double balance = totalIncome - totalExpense;
        Budget budget = budgetRepository
        .findByUserId(user.getId())
        .orElse(null);

double monthlyBudget = 0;

if (budget != null) {
    monthlyBudget = budget.getMonthlyBudget();
}

double remainingBudget = monthlyBudget - totalExpense;

double budgetUsed = 0;

if (monthlyBudget > 0) {
    budgetUsed = (totalExpense / monthlyBudget) * 100;
}

model.addAttribute("monthlyBudget", monthlyBudget);
model.addAttribute("remainingBudget", remainingBudget);
model.addAttribute("budgetUsed", budgetUsed);

        List<Income> incomes =
        incomeRepository.findByUserIdOrderByDateDesc(user.getId());

List<Expense> expenses =
        expenseRepository.findByUserIdOrderByDateDesc(user.getId());

        double biggestIncome = incomes.stream()
        .mapToDouble(Income::getAmount)
        .max()
        .orElse(0);

double biggestExpense = expenses.stream()
        .mapToDouble(Expense::getAmount)
        .max()
        .orElse(0);

int totalTransactions = incomes.size() + expenses.size();

double averageExpense = expenses.stream()
        .mapToDouble(Expense::getAmount)
        .average()
        .orElse(0);

        double food = 0;
double shopping = 0;
double bills = 0;
double transport = 0;
double other = 0;

for (Expense expense : expenses) {

    if (expense.getCategory() == null) continue;

    switch (expense.getCategory().trim().toLowerCase()) {

        case "food":
            food += expense.getAmount();
            break;

        case "shopping":
            shopping += expense.getAmount();
            break;

        case "bills":
            bills += expense.getAmount();
            break;

        case "transport":
            transport += expense.getAmount();
            break;

        default:
            other += expense.getAmount();
    }
}

        List<String> months = new ArrayList<>();
        List<Double> incomeData = new ArrayList<>();
        List<Double> expenseData = new ArrayList<>();

        for (Month month : Month.values()) {

            months.add(
                    month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            );

            double incomeTotal = incomes.stream()
                    .filter(i -> i.getDate() != null && i.getDate().getMonth() == month)
                    .mapToDouble(Income::getAmount)
                    .sum();

            double expenseTotal = expenses.stream()
                    .filter(e -> e.getDate() != null && e.getDate().getMonth() == month)
                    .mapToDouble(Expense::getAmount)
                    .sum();

            incomeData.add(incomeTotal);
            expenseData.add(expenseTotal);

        }

        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("balance", balance);
        model.addAttribute("biggestIncome", biggestIncome);
model.addAttribute("biggestExpense", biggestExpense);
model.addAttribute("totalTransactions", totalTransactions);
model.addAttribute("averageExpense", averageExpense);

        model.addAttribute("months", months);
        model.addAttribute("incomeData", incomeData);
        model.addAttribute("expenseData", expenseData);
        model.addAttribute(
        "expenseBreakdown",
        List.of(food, shopping, bills, transport, other)
);
    }



@GetMapping("/settings")
public String settings(Model model, HttpSession session) {

    User user = (User) session.getAttribute("loggedInUser");

    if (user == null) {
        return "redirect:/login";
    }

    model.addAttribute("user", user);

    return "settings";
}

}