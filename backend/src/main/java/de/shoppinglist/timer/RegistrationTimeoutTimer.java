package de.shoppinglist.timer;

import de.shoppinglist.entity.ConfirmationToken;
import de.shoppinglist.entity.User;
import de.shoppinglist.repository.ConfirmationTokenRepository;
import de.shoppinglist.repository.UserRepository;
import de.shoppinglist.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
public class RegistrationTimeoutTimer {
    private static final Logger log = LoggerFactory.getLogger(RegistrationTimeoutTimer.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final EmailService emailService;
    private final ConfirmationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public RegistrationTimeoutTimer(EmailService emailService, ConfirmationTokenRepository tokenRepository, UserRepository userRepository) {
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 3_600_000) // every hour
    public void deleteOldToken() {
        log.info("Timer Old Token is now running ({})", dateFormat.format(new Date()));

        List<ConfirmationToken> tokensExpiredOrConfirmed = tokenRepository.findByExpiresAtBeforeOrConfirmedAtNotNull(LocalDateTime.now());
        tokenRepository.deleteAll(tokensExpiredOrConfirmed);

        log.info("Timer Old Token is now finished ({})", dateFormat.format(new Date()));
    }

    @Scheduled(fixedRate = 3_600_000) // every hour
    public void deleteOldUsers() {
        log.info("Timer Old Users is now running ({})", dateFormat.format(new Date()));

        String emailText = "Ihr Account wurde aufgrund von Inaktivität gelöscht.<br/>Bitte erstellen Sie bei Bedarf einen neuen Account.";

        List<User> timedoutUsers = userRepository.findByLastLoggedInNullAndCreatedAtBefore(LocalDateTime.now().minusDays(1)); // vor mind. einem Tag erstellt, aber noch nie eingeloggt
        timedoutUsers.forEach(user -> emailService.sendEmail(user.getEmail(), "Shopping-List: Account wurde gelöscht", emailText));
        userRepository.deleteAll(timedoutUsers);

        List<User> oldUsersWithoutData = userRepository.findByLastLoggedInBeforeAndEinkaufszettelsOwnerEmptyAndEinkaufszettelsSharedWithEmpty(LocalDateTime.now().minusDays(180)); // inaktive User die keinen Einkaufszettel besitzen
        oldUsersWithoutData.forEach(user -> emailService.sendEmail(user.getEmail(), "Shopping-List: Account wurde gelöscht", emailText));
        userRepository.deleteAll(oldUsersWithoutData);

        log.info("Timer Old Users is now finished ({})", dateFormat.format(new Date()));
    }
}
