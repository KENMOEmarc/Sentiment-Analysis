package ken.tar.sa_backend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ken.tar.sa_backend.entity.Client;
import ken.tar.sa_backend.entity.Email;
import ken.tar.sa_backend.entity.Sentiment;
import ken.tar.sa_backend.enums.TypeSentiment;
import ken.tar.sa_backend.repository.SentimentRepository;
import ken.tar.sa_backend.service.AiService;
import ken.tar.sa_backend.service.ClientService;
import ken.tar.sa_backend.service.EmailService;
import ken.tar.sa_backend.service.SentimentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class SentimentServiceImpl implements SentimentService {

    private static final Logger logger = LoggerFactory.getLogger(SentimentServiceImpl.class);

    private final TransactionTemplate transactionTemplate;
    private final SentimentRepository sentimentRepository;
    private final ClientService clientService;
    private final AiService aiService;
    private final EmailService emailService;

    @Value("${app.prompt.sentiment}")
    private String sentimentPrompt;

    @Value("${app.prompt.email}")
    private String emailPrompt;

    @Value("${APP_EMAIL}")
    private String appEmail;

    @Autowired
    public SentimentServiceImpl(SentimentRepository sentimentRepository, ClientService clientService, AiService aiService, EmailService emailService, TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
        this.sentimentRepository = sentimentRepository;
        this.clientService = clientService;
        this.aiService = aiService;
        this.emailService = emailService;
    }

    @Override
    public CompletableFuture<Void> save(Sentiment sentiment) {
        String prompt = sentimentPrompt.formatted(sentiment.getText());

        return aiService.chatAsync(prompt)
                .thenApply(this::parseSentiment)
                .thenAccept(type -> {
                    transactionTemplate.executeWithoutResult(status -> {
                        Client client = clientService.readOrCreate(sentiment.getClient());
                        sentiment.setClient(client);
                        sentiment.setSentiment(type);
                        sentimentRepository.save(sentiment);
                    });
                })
                .thenCompose(v -> notify(sentiment))
                .exceptionally(ex -> {
                    logger.error("Error in sentiment pipeline", ex);
                    return null;
                });
    }

    private TypeSentiment parseSentiment(String response) {
        try {
            return TypeSentiment.valueOf(response.trim().toUpperCase());
        } catch (Exception e) {
            logger.error("Invalid sentiment response from AI: {}", response);
            return TypeSentiment.NEUTRE; // fallback safe
        }
    }

    private CompletableFuture<Void> notify(Sentiment sentiment) {

        String clientMail = sentiment.getClient().getEmail();
        String prompt = emailPrompt.formatted(
                "Avis utilisateur sur vos services : " + sentiment.getText()
        );

        CompletableFuture<Void> clientEmailFuture = aiService.generateEmailAsync(prompt)
                .thenCompose(email -> {
                    email.setTo(clientMail);
                    email.setFrom(appEmail);
                    return emailService.sendEmail(email)
                            .thenAccept(success -> {
                                if (!success) {
                                    logger.warn("Client email not sent for sentiment");
                                }
                            })
                            .exceptionally(ex -> {
                                logger.error("Failed to send client email", ex);
                                return null;
                            });
                });

        String body = """
                Un nouvel avis utilisateur a été reçu : %s
                De la part de %s
                """.formatted(sentiment.getText(), sentiment.getClient().getEmail());

        Email adminMail = new Email();
        adminMail.setFrom(appEmail);
        adminMail.setTo(appEmail);
        adminMail.setSubject("Nouveau sentiment reçu");
        adminMail.setBody(body);

        CompletableFuture<Void> adminEmailFuture = emailService.sendEmail(adminMail)
                .thenAccept(success -> {
                    if (!success) {
                        logger.warn("Admin email not sent for sentiment");
                    }
                })
                .exceptionally(ex -> {
                    logger.error("Failed to send admin email", ex);
                    return null;
                });

        return CompletableFuture.allOf(clientEmailFuture, adminEmailFuture);
    }

    @Override
    public List<Sentiment> getSentiments() {
        return sentimentRepository.findAll();
    }

    @Override
    public void delete(long id) {
        sentimentRepository.deleteById(id);
    }

    @Override
    public Sentiment getSentiment(Long id) {
        return sentimentRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Aucun sentiment n'existe avec l'id " + id)
                );
    }
}