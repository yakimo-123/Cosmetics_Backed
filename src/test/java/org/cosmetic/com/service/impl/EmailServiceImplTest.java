package org.cosmetic.com.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl(mailSender, templateEngine);
        ReflectionTestUtils.setField(emailService, "from", "test@example.com");
    }

    @Nested
    @DisplayName("Send Simple Email Tests")
    class SendSimpleEmailTests {

        @Test
        @DisplayName("Should successfully send simple email")
        void shouldSuccessfullySendSimpleEmail() {
            // Given
            String to = "recipient@example.com";
            String subject = "Test Subject";
            String text = "Test Message";

            doNothing().when(mailSender).send(any(SimpleMailMessage.class));

            // When
            assertDoesNotThrow(() -> emailService.sendSimpleEmail(to, subject, text));

            // Then
            verify(mailSender).send(any(SimpleMailMessage.class));
        }

        @Test
        @DisplayName("Should throw AppException when simple email sending fails")
        void shouldThrowAppExceptionWhenSimpleEmailSendingFails() {
            // Given
            String to = "recipient@example.com";
            String subject = "Test Subject";
            String text = "Test Message";

            doThrow(new RuntimeException("Email sending failed"))
                .when(mailSender).send(any(SimpleMailMessage.class));

            // When & Then
            AppException exception = assertThrows(AppException.class,
                () -> emailService.sendSimpleEmail(to, subject, text));
            assertEquals(ErrorCode.EMAIL_SEND_FAILED, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Send Email With Attachment Tests")
    class SendEmailWithAttachmentTests {

        @Test
        @DisplayName("Should successfully send email with attachment")
        void shouldSuccessfullySendEmailWithAttachment() {
            // Given
            String to = "recipient@example.com";
            String subject = "Test Subject";
            String text = "Test Message";
            File attachment = new File("test.txt");

            MimeMessage mimeMessage = mock(MimeMessage.class);
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

            // When
            assertDoesNotThrow(() -> emailService.sendEmailWithAttachment(to, subject, text, attachment));

            // Then
            verify(mailSender).createMimeMessage();
            verify(mailSender).send(any(MimeMessage.class));
        }

        @Test
        @DisplayName("Should throw AppException when attachment email sending fails")
        void shouldThrowAppExceptionWhenAttachmentEmailSendingFails() {
            // Given
            String to = "recipient@example.com";
            String subject = "Test Subject";
            String text = "Test Message";
            File attachment = new File("test.txt");

            MimeMessage mimeMessage = mock(MimeMessage.class);
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            doAnswer(invocation -> {
                throw new MessagingException("Simulated failure");
            }).when(mailSender).send(any(MimeMessage.class));

            // When & Then
            AppException exception = assertThrows(AppException.class,
                () -> emailService.sendEmailWithAttachment(to, subject, text, attachment));
            assertEquals(ErrorCode.EMAIL_SEND_FAILED, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Send Verification Email Tests")
    class SendVerificationEmailTests {

        @Test
        @DisplayName("Should successfully send verification email")
        void shouldSuccessfullySendVerificationEmail() {
            // Given
            String to = "recipient@example.com";
            String verificationCode = "123456";
            String processedContent = "<html>Verification Email Content</html>";

            MimeMessage mimeMessage = mock(MimeMessage.class);
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(templateEngine.process(anyString(), any())).thenReturn(processedContent);

            // When
            assertDoesNotThrow(() -> emailService.sendVerificationEmail(to, verificationCode));

            // Then
            verify(mailSender).createMimeMessage();
            verify(mailSender).send(any(MimeMessage.class));
            verify(templateEngine).process(eq("verify-email"), any());
        }

        @Test
        @DisplayName("Should throw AppException when verification email sending fails")
        void shouldThrowAppExceptionWhenVerificationEmailSendingFails() {
            // Given
            String to = "recipient@example.com";
            String verificationCode = "123456";
            String processedContent = "<html>Verification Email Content</html>";

            MimeMessage mimeMessage = mock(MimeMessage.class);
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(templateEngine.process(anyString(), any())).thenReturn(processedContent);
            doThrow(new RuntimeException("Failed to send email"))
                .when(mailSender).send(any(MimeMessage.class));

            // When & Then
            AppException exception = assertThrows(AppException.class,
                () -> emailService.sendVerificationEmail(to, verificationCode));
            assertEquals(ErrorCode.EMAIL_VERIFICATION_FAILED, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Send Forgot Password Email Tests")
    class SendForgotPasswordEmailTests {

        @Test
        @DisplayName("Should successfully send forgot password email")
        void shouldSuccessfullySendForgotPasswordEmail() {
            // Given
            String to = "recipient@example.com";
            String verificationCode = "123456";
            String processedContent = "<html>Reset Password Email Content</html>";

            MimeMessage mimeMessage = mock(MimeMessage.class);
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(templateEngine.process(anyString(), any())).thenReturn(processedContent);

            // When
            assertDoesNotThrow(() -> emailService.sendForgotPasswordEmail(to, verificationCode));

            // Then
            verify(mailSender).createMimeMessage();
            verify(mailSender).send(any(MimeMessage.class));
            verify(templateEngine).process(eq("forgot-password"), any());
        }

        @Test
        @DisplayName("Should throw AppException when forgot password email sending fails")
        void shouldThrowAppExceptionWhenForgotPasswordEmailSendingFails() {
            // Given
            String to = "recipient@example.com";
            String verificationCode = "123456";
            String processedContent = "<html>Reset Password Email Content</html>";

            MimeMessage mimeMessage = mock(MimeMessage.class);
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(templateEngine.process(anyString(), any())).thenReturn(processedContent);
            doThrow(new RuntimeException("Failed to send email"))
                .when(mailSender).send(any(MimeMessage.class));

            // When & Then
            AppException exception = assertThrows(AppException.class,
                () -> emailService.sendForgotPasswordEmail(to, verificationCode));
            assertEquals(ErrorCode.EMAIL_FORGOT_FAILED, exception.getErrorCode());
        }
    }
}