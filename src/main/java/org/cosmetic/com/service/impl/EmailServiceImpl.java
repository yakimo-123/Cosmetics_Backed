package org.cosmetic.com.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cosmetic.com.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    public EmailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }


    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }


    @Override
    public void sendEmailWithAttachment(String to, String subject, String text, File file) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            helper.addAttachment(file.getName(), file);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Can't send mail with attachment", e);
        }
    }

    @Override
    public void sendVerificationEmail(String to, String verificationCode) {
        Context context = new Context();
        String verifyUrl = "http://localhost:8080/api/auth/verify-email?code=" + verificationCode;
        context.setVariable("verifyUrl", verifyUrl);

        String content = templateEngine.process("verify-email", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject("Xác minh tài khoản Cosmetic Store");
            helper.setText(content, true);


            mailSender.send(message);
        } catch (Exception e) {
            log.error("Error sending verification email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Can't send email verification", e);
        }
    }

}
