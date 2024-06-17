package de.shoppinglist.service;

import de.shoppinglist.exception.ValidationException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

            String emailText = "Sehr geehrte Damen und Herren,<br/><br/>" + body + "<br/><br/>Mit freundlichen Grüßen,<br/>Alice Bedow";

            mimeMessageHelper.setText(emailText, true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setFrom("shoppinglist@example.com");

            mailSender.send(mimeMessage);
            log.debug("E-Mail wurde versendet an {} mit dem Titel {} und dem Inhalt {}", to, subject, body);
        } catch (MessagingException e) {
            log.error("Fehler beim Mail versenden: ", e);
            throw new ValidationException("Fehler beim E-Mail versenden");
        }
    }
}
