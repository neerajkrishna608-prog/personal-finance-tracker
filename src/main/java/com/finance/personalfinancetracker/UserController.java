package com.finance.personalfinancetracker;

import com.finance.personalfinancetracker.model.User;
import com.finance.personalfinancetracker.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;


    // ================= REGISTER =================

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }


    @PostMapping("/register")
    public String registerUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password) {

        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        userRepository.save(user);

        return "redirect:/";
    }


    // ================= LOGIN =================

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }


    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        User user =
                userRepository.findByEmailAndPassword(email, password);

        if (user != null) {

            session.setAttribute("loggedInUser", user);

            return "redirect:/dashboard";

        } else {

            model.addAttribute(
                    "error",
                    "Invalid Email or Password"
            );

            return "login";
        }
    }


    // ================= LOGOUT =================

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }


    // ================= FORGOT PASSWORD =================

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }


    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestParam String email,
            Model model) {

        User user = userRepository.findByEmail(email);

        if (user == null) {

            model.addAttribute(
                    "error",
                    "No account found with this email."
            );

            return "forgot-password";
        }


        // Generate unique reset token
        String resetToken = UUID.randomUUID().toString();

        // Save token
        user.setResetToken(resetToken);

        // Token expires in 15 minutes
        user.setResetTokenExpiry(
                LocalDateTime.now().plusMinutes(15)
        );

        userRepository.save(user);


        // Create reset link
        String resetLink =
                "http://localhost:8080/reset-password?token="
                        + resetToken;


        // Send email
        emailService.sendPasswordResetEmail(
                user.getEmail(),
                resetLink
        );


        model.addAttribute(
                "success",
                "A password reset link has been sent to your email."
        );

        return "forgot-password";
    }


    // ================= RESET PASSWORD PAGE =================

    @GetMapping("/reset-password")
    public String showResetPasswordPage(
            @RequestParam String token,
            Model model) {

        User user = userRepository.findByResetToken(token);

        // Check if token exists
        if (user == null) {

            model.addAttribute(
                    "error",
                    "Invalid password reset link."
            );

            return "reset-password";
        }


        // Check if token expired
        if (user.getResetTokenExpiry()
                .isBefore(LocalDateTime.now())) {

            model.addAttribute(
                    "error",
                    "This password reset link has expired."
            );

            return "reset-password";
        }


        // Send token to HTML page
        model.addAttribute("token", token);

        return "reset-password";
    }


    // ================= RESET PASSWORD =================

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model) {

        User user = userRepository.findByResetToken(token);


        // Validate token
        if (user == null) {

            model.addAttribute(
                    "error",
                    "Invalid password reset link."
            );

            return "reset-password";
        }


        // Check expiry
        if (user.getResetTokenExpiry()
                .isBefore(LocalDateTime.now())) {

            model.addAttribute(
                    "error",
                    "This password reset link has expired."
            );

            return "reset-password";
        }


        // Check passwords match
        if (!newPassword.equals(confirmPassword)) {

            model.addAttribute(
                    "error",
                    "Passwords do not match."
            );

            model.addAttribute("token", token);

            return "reset-password";
        }


        // Update password
        user.setPassword(newPassword);

        // Remove used token
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);


        return "redirect:/login?resetSuccess=true";
    }


    // ================= CHANGE PASSWORD =================

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            Model model) {

        User user =
                (User) session.getAttribute("loggedInUser");


        if (user == null) {
            return "redirect:/login";
        }


        if (!user.getPassword().equals(currentPassword)) {

            model.addAttribute(
                    "passwordError",
                    "Current password is incorrect."
            );

            model.addAttribute("user", user);

            return "settings";
        }


        if (!newPassword.equals(confirmPassword)) {

            model.addAttribute(
                    "passwordError",
                    "New passwords do not match."
            );

            model.addAttribute("user", user);

            return "settings";
        }


        if (newPassword.trim().isEmpty()) {

            model.addAttribute(
                    "passwordError",
                    "New password cannot be empty."
            );

            model.addAttribute("user", user);

            return "settings";
        }


        user.setPassword(newPassword);

        userRepository.save(user);


        session.setAttribute("loggedInUser", user);


        model.addAttribute(
                "passwordSuccess",
                "Password changed successfully."
        );

        model.addAttribute("user", user);

        return "settings";
    }

}