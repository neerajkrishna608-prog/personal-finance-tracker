package com.finance.personalfinancetracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;


    public void sendPasswordResetEmail(String toEmail, String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject("Reset Your Personal Finance Tracker Password");

        message.setText(
                "Hello,\n\n" +
                "We received a request to reset your Personal Finance Tracker password.\n\n" +
                "Click the link below to reset your password:\n\n" +
                resetLink +
                "\n\nThis link will expire in 15 minutes.\n\n" +
                "If you did not request a password reset, you can safely ignore this email.\n\n" +
                "Personal Finance Tracker"
        );

        mailSender.send(message);
    }
}