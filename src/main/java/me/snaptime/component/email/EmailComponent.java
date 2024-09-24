package me.snaptime.component.email;

import jakarta.mail.MessagingException;

public interface EmailComponent {

    String sendVerificationCode(String toEmail, String verificationCode) throws MessagingException;
}
