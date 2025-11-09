package com.maven.neuto.utils;


import com.maven.neuto.exception.APIException;
import com.maven.neuto.model.Otpverify;
import com.maven.neuto.model.User;
import com.maven.neuto.repository.OtpVerifyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Component
@RequiredArgsConstructor
public class HelperMethod {
    private static final int OTP_EXPIRY_MINUTES = 2;
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);
    private final OtpVerifyRepository otpVerifyRepository;
    private final EmailService emailService;


    public static String getBrowserName(String userAgentHeader){
        String ua = userAgentHeader.toLowerCase();

        if (ua.contains("edg")) return "Edge"; // must be checked before Chrome
        if (ua.contains("opr") || ua.contains("opera")) return "Opera"; // before Chrome
        if (ua.contains("chrome") && !ua.contains("edg") && !ua.contains("opr")) return "Chrome";
        if (ua.contains("safari") && !ua.contains("chrome")) return "Safari"; // exclude Chrome
        if (ua.contains("firefox")) return "Firefox";
        if (ua.contains("msie") || ua.contains("trident")) return "Internet Explorer";

        return "Unknown";
    }

    public static Integer GenerateOtp(){
        return (int) ((Math.random() * 9000) + 1000);
    }

    public void handleOtpProcess(User user, String subject, String template) {
        Optional<Otpverify> existingOpt = otpVerifyRepository.findByEmail(user.getEmail());

        int otp;
        if (existingOpt.isPresent()) {
            Otpverify existing = existingOpt.get();
            if (existing.getExpiresAt().isBefore(LocalDateTime.now())) {
                // OTP expired → generate new one
                otp = GenerateOtp();
                existing.setOpt(otp);
                existing.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
                otpVerifyRepository.save(existing);
            } else {
                // OTP still valid → resend the same one
                otp = existing.getOpt();
            }
        } else {
            // No OTP exists → create new one
            otp = GenerateOtp();
            Otpverify otpVerify = new Otpverify();
            otpVerify.setEmail(user.getEmail());
            otpVerify.setOpt(otp);
            otpVerify.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
            otpVerifyRepository.save(otpVerify);
        }

        // Send OTP email
        sendOtpEmail(user.getEmail(), otp, subject, template);
    }

    private Boolean sendOtpEmail(String email, int otp, String subject, String template) {
        String body = template.replace("{{otp}}", String.valueOf(otp));
        try {
            emailService.sendEmail(email, subject, body).get();
            return true;
        } catch (Exception e) {
            log.info("Failed to send OTP email: {}", e.getMessage());
             throw new APIException("send.email.failed", HttpStatus.BAD_REQUEST);
        }
    }

    public static List<String> extractEmailsFromCSV(MultipartFile file) {
        List<String> emails = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            // Read header line manually instead of letting CSV parser fail
            String headerLine = reader.readLine();
            if (headerLine == null || !headerLine.toLowerCase().contains("email")) {
                throw new APIException("CSV must contain a header named 'email'", HttpStatus.BAD_REQUEST);
            }


            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader("email") // Explicit header to avoid auto-detection issues
                    .withSkipHeaderRecord()
                    .parse(reader);

            log.info("records: {}", records);
            for (CSVRecord record : records) {
                String email = record.get("email").trim().toLowerCase();
                log.info("records email: {}", email);
                if (StringUtils.hasText(email)) {
                    emails.add(email);
                }
            }
        } catch (IOException e) {
            throw new APIException("CSV parsing failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return emails;
    }

    public String encryptEmail(String email) {
        // placeholder – implement real encryption
        return Base64.getEncoder().encodeToString(email.getBytes());
    }

    public static boolean isValid(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = PATTERN.matcher(email);
        return matcher.matches();
    }

    public String decryptEmail(String encryptedEmail) {
        // placeholder – implement real decryption
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedEmail);
        return new String(decodedBytes);
    }

    public String randomTokenGenerator(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            token.append(characters.charAt(index));
        }
        return token.toString();
    }


}
