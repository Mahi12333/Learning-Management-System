package com.maven.neuto.config;

import com.maven.neuto.emun.ProfileComplete;
import com.maven.neuto.emun.Status;
import com.maven.neuto.emun.Step;
import com.maven.neuto.model.Role;
import com.maven.neuto.model.User;
import com.maven.neuto.repository.RoleRepository;
import com.maven.neuto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@RequiredArgsConstructor
public class SeedDataConfig {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    @Bean
    CommandLineRunner seedDatabase() {
        return args -> {
            // 1. Seed roles
            String[] roles = {"SUPER-ADMIN", "ADMIN", "USER", "TEACHER"};
            for (String r : roles) {
                roleRepo.findByName(r).orElseGet(() -> roleRepo.save(new Role(null, r)));
            }

            // 2. Seed Super Admin
            String email = "sipore2779@canvect.com";
            if (userRepo.findByEmail(email).isEmpty()) {
                Role role = roleRepo.findByName("TEACHER")
                        .orElseThrow(() -> new RuntimeException("Role not found"));

                User user = new User();
                user.setFirstName("Sipore");
                user.setLastName("Giri");
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode("Mahitosh@123"));
                user.setRole(role);
                user.setStatus(Status.ACTIVE);
                user.setProfileComplete(ProfileComplete.COMPLETE);
                user.setUserName("sipore2779@");
                user.setStep(Step.THREE);

                userRepo.save(user);
                System.out.println("✅ Super Admin seeded: " + email);
            } else {
                System.out.println("ℹ️ Super Admin already exists: " + email);
            }
        };
    }
}
