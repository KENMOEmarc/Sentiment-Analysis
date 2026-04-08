package ken.tar.sa_backend.service.impl;

import ken.tar.sa_backend.config.LoggerFactory;
import ken.tar.sa_backend.entity.Email;
import ken.tar.sa_backend.service.EmailService;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class EmailServiceImpl implements EmailService {

    private final Logger logger;
    private final JavaMailSender mailSender;
    private final Executor asyncExecutor;

    public EmailServiceImpl(LoggerFactory loggerFactory, JavaMailSender mailSender, @Qualifier("taskExecutor") Executor asyncExecutor) {
        this.logger = loggerFactory.getLogger(EmailServiceImpl.class);
        this.mailSender = mailSender;
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public CompletableFuture<Boolean> sendEmail(Email emailDetails) {
        logger.info("Preparing to send email to {}", emailDetails.getTo());
        return CompletableFuture.supplyAsync(() -> {
            validate(emailDetails);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailDetails.getFrom());
            message.setTo(emailDetails.getTo());
            message.setSubject(emailDetails.getSubject());
            message.setText(emailDetails.getBody());

            mailSender.send(message);
            return true;
        }, asyncExecutor);
    }

    private void validate(Email emailDetails) {
        logger.info("Validating email details for recipient {}", emailDetails.getTo());
        Objects.requireNonNull(emailDetails, "emailDetails must not be null");

        if (emailDetails.getTo() == null || emailDetails.getTo().isEmpty()) {
            throw new IllegalArgumentException("Recipient email is required");
        }
        if (emailDetails.getSubject() == null || emailDetails.getSubject().isBlank()) {
            throw new IllegalArgumentException("Email subject is required");
        }
        if (emailDetails.getBody() == null || emailDetails.getBody().isBlank()) {
            throw new IllegalArgumentException("Email body is required");
        }
    }
}
