package de.shoppinglist.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(String to, String subject, String body) {
        log.info("Sending email to {} with subject {} and body {}", to, subject, body); // Dummy-Implementierung: Durch echten Mailserver austauschen
    }
}
