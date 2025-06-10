package org.cosmetic.com.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cosmetic.com.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;


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
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Xác minh địa chỉ email của bạn");

            String verifyUrl = "http://localhost:3000/verify?code=" + verificationCode;

            String content = """
            <html>
                <body>
                    <h3>Chào bạn,</h3>
                    <p>Đây là mã xác minh tài khoản của bạn:</p>
                    <h2 style="color: #2e6c80;">%s</h2>
                    <p>Hoặc bạn có thể nhấn vào đường dẫn bên dưới để xác minh:</p>
                    <a href="%s">Xác minh ngay</a>
                    <br/><br/>
                    <p style="font-size:12px;color:#888;">Mã này sẽ hết hạn sau 5 phút.</p>
                </body>
            </html>
        """.formatted(verificationCode, verifyUrl);

            helper.setText(content, true); // true để gửi HTML
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Error sending verification email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Can't send email verification", e);
        }
    }

}
