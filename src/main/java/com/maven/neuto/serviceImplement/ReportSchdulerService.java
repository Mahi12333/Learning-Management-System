package com.maven.neuto.serviceImplement;

import com.maven.neuto.model.User;
import com.maven.neuto.repository.UserRepository;
import com.maven.neuto.utils.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportSchdulerService {
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 27 19 * * *")
    public void sendEmailIncompleteUser() {
        /*
        ✔ thenApply()
        Result milta → Modify karke naya result return karte ho.
        ✔ thenAccept()
        Result milta → Sirf use karte ho, return kuch nahi.
        ✔ thenRun()
        Koi result nahi → sirf ek action run karte ho.
         */

        // 1. Fetch all users whose profile is incomplete
        List<User> incompleteUsers = userRepository.findAllByIncompleteProfile();

        if (incompleteUsers.isEmpty()) {
            log.info("No incomplete users found for reporting.");
            return;
        }

        log.info("Found {} incomplete users. Sending emails...", incompleteUsers.size());

        // 2. Loop through each user & call async mail
        for (User user : incompleteUsers) {

            String to = user.getEmail();
            String subject = "Complete Your Profile";
            String body = "<h2>Hello " + user.getFirstName() + ",</h2>"
                    + "<p>Your profile is still incomplete. Please update it.</p>";

            CompletableFuture<Boolean> future = emailService.sendEmail(to, subject, body);

            future.thenAccept(success -> {
                if (success) {
                    log.info("Email sent to user {} ({})", user.getFirstName(), user.getEmail());
                } else {
                    log.warn("Email failed for user {} ({})", user.getFirstName(), user.getEmail());
                }
            });
        }
    }
}
