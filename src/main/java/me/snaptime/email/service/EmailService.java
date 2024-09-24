package me.snaptime.email.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendCodeToEmail(String toEmail) throws MessagingException;
    boolean verifyCode(String email, String verificationCode);

}
