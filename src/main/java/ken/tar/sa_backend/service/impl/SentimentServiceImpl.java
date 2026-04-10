package ken.tar.sa_backend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import ken.tar.sa_backend.config.LoggerFactory;
import ken.tar.sa_backend.entity.Client;
import ken.tar.sa_backend.entity.Email;
import ken.tar.sa_backend.entity.Sentiment;
import ken.tar.sa_backend.enums.TypeSentiment;
import ken.tar.sa_backend.repository.SentimentRepository;
import ken.tar.sa_backend.service.AiService;
import ken.tar.sa_backend.service.ClientService;
import ken.tar.sa_backend.service.EmailService;
import ken.tar.sa_backend.service.SentimentService;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class SentimentServiceImpl implements SentimentService {

    private final Logger logger;
    private final TransactionTemplate transactionTemplate;
    private final SentimentRepository theSentimentRepository;
    private final ClientService theClientService;
    private final AiService theAiService;
    private final EmailService theEmailService;

    @Value("${app.prompt.sentiment}")
    private String sentimentPrompt;

    @Value("${app.prompt.email}")
    private String emailPrompt;

    @Value("${APP_EMAIL}")
    private String appEmail;

    @Autowired
    public SentimentServiceImpl(LoggerFactory loggerFactory, SentimentRepository theSentimentRepository, ClientService theClientService, AiService aiService, EmailService theEmailService, TransactionTemplate transactionTemplate) {
        this.logger = loggerFactory.getLogger(SentimentServiceImpl.class);
        this.transactionTemplate = transactionTemplate;
        this.theSentimentRepository = theSentimentRepository;
        this.theClientService = theClientService;
        this.theAiService = aiService;
        this.theEmailService = theEmailService;
    }

    @Override
    public CompletableFuture<Void> save(Sentiment sentiment) {
        String prompt = sentimentPrompt.formatted(sentiment.getText());

        return theAiService.chatAsync(prompt)
                .thenApply(this::parseSentiment)
                .thenAccept(type -> {
                    transactionTemplate.executeWithoutResult(status -> {
                        Client client = theClientService.readOrCreate(sentiment.getClient());
                        sentiment.setClient(client);
                        sentiment.setSentiment(type);
                        logger.info("Saving sentiment for client {}",
                                sentiment.getClient().getEmail()
                        );
                        theSentimentRepository.save(sentiment);
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

        logger.info("Generating email for client {}", clientMail);
        CompletableFuture<Void> clientEmailFuture = theAiService.generateEmailAsync(prompt)
                .thenCompose(email -> {
                    email.setTo(clientMail);
                    email.setFrom(appEmail);
                    logger.info("Sending email for client {}", clientMail);
                    return theEmailService.sendEmail(email)
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

        logger.info("Sending admin email for new sentiment from client {}", clientMail);
        CompletableFuture<Void> adminEmailFuture = theEmailService.sendEmail(adminMail)
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
        logger.info("Retrieving all sentiments");
        return theSentimentRepository.findAll();
    }

    @Override
    public void delete(long id) {
        logger.info("Deleting sentiment with id {}", id);
        theSentimentRepository.deleteById(id);
    }

    @Override
    public Sentiment getSentiment(Long id) {
        return theSentimentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Sentiment with id {} not found", id);
                    return new EntityNotFoundException("Aucun sentiment n'existe avec l'id " + id);
                });
    }
}