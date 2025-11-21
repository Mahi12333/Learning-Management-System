package com.maven.neuto.utils;


import com.maven.neuto.exception.APIException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Component
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.properties.domain_name}") // Domain name from application properties
    private String domainName;

    @Value("${spring.mail.username}") // Domain name from application properties
    private String fromEmail;


   /* @Async
    public CompletableFuture<Boolean> sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            //helper.setFrom(fromEmail != null ? fromEmail : domainName);
            // Set friendly sender name
            String senderName = "Lernera";
            helper.setFrom(new InternetAddress(fromEmail, senderName));
            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
            return CompletableFuture.completedFuture(true);
        } catch (MessagingException | UnsupportedEncodingException e ) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new APIException("send.email.otp.failed", HttpStatus.BAD_REQUEST); // Localized key
        }
    }*/

    @Async
    public CompletableFuture<String> sendEmail(String to, String subject, String htmlContent) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);

                String senderName = "Lernera";
                helper.setFrom(new InternetAddress(fromEmail, senderName));

                mailSender.send(message);
                log.info("Email sent successfully to {}", to);

                return "send.email.otp.success"; // Success

            } catch (MessagingException | UnsupportedEncodingException e) {
                // This exception will be handled in exceptionally()
                throw new RuntimeException("Email sending failed: " + e.getMessage(), e);
            }

        }).exceptionally(ex -> {
            log.error("Async email error: {}", ex.getMessage());
            throw new APIException("send.email.otp.failed", HttpStatus.BAD_REQUEST);
            // return false;
        });
    }

}
