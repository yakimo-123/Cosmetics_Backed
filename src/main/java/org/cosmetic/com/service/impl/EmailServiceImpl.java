package org.cosmetic.com.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cosmetic.com.exception.EmailException;
import org.cosmetic.com.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
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


    @Async
    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Async
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
            throw new EmailException("Can't send mail with attachment", e);
        }
    }


    @Async
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
            throw new EmailException("Can't send email verification", e);
        }
    }

    @Async
    @Override
    public void sendForgotPasswordEmail(String to, String verificationCode) {
        Context context = new Context();
        String resetUrl = "http://localhost:8080/api/auth/reset-password?code=" + verificationCode;
        context.setVariable("resetPasswordUrl", resetUrl);
        String content = templateEngine.process("forgot-password", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject("Yêu cầu đặt lại mật khẩu");
            helper.setText(content, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Error sending forgot password email to {}: {}", to, e.getMessage());
            throw new EmailException("Can't send forgot password email", e);
        }
    }
}
