// package com.mydev.ecommerce.email.service;

// import jakarta.mail.internet.MimeMessage;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.mail.javamail.MimeMessageHelper;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Service;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class EmailService {

//     private final JavaMailSender mailSender;

//     @Value("${app.mail.enabled:true}")
//     private boolean mailEnabled;

//     @Value("${app.mail.from-email}")
//     private String fromEmail;

//     @Value("${app.mail.from-name:Trendz Firenze}")
//     private String fromName;

//     @Async("mailExecutor")
//     public void sendHtmlEmail(String to, String subject, String htmlBody) {
//         if (!mailEnabled) {
//             log.info("Mail disabled. Skipping email to {}", to);
//             return;
//         }

//         try {
//             MimeMessage message = mailSender.createMimeMessage();
//             MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
//             helper.setFrom(fromEmail, fromName);
//             helper.setTo(to);
//             helper.setSubject(subject);
//             helper.setText(htmlBody, true);
//             mailSender.send(message);
//             log.info("Email sent successfully to {}", to);
//         } catch (Exception e) {
//             log.error("Failed to send email to {}", to, e);
//         }
//     }
// }


package com.mydev.ecommerce.email.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${app.mail.from-email}")
    private String fromEmail;

    @Value("${app.mail.from-name:Trendz Firenze}")
    private String fromName;

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        log.info("MAIL FLOW START -> enabled={}, to={}, subject={}", mailEnabled, to, subject);

        if (!mailEnabled) {
            log.warn("Mail disabled. Skipping email to {}", to);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);

            log.info("EMAIL SENT SUCCESS -> to={}", to);
        } catch (Exception e) {
            log.error("EMAIL FAILED -> to={}", to, e);
            throw new RuntimeException("Failed to send email to " + to, e);
        }
    }
}