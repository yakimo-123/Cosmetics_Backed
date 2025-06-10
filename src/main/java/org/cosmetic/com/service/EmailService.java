package org.cosmetic.com.service;

import java.io.File;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String text);
    void sendEmailWithAttachment(String to, String subject, String text, File attachment);
    public void sendVerificationEmail(String to, String verificationCode);
}
