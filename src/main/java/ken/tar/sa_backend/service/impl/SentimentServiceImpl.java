package ken.tar.sa_backend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import ken.tar.sa_backend.entity.Client;
import ken.tar.sa_backend.entity.Email;
import ken.tar.sa_backend.entity.Sentiment;
import ken.tar.sa_backend.enums.TypeSentiment;
import ken.tar.sa_backend.repository.SentimentRepository;
import ken.tar.sa_backend.service.AiService;
import ken.tar.sa_backend.service.ClientService;
import ken.tar.sa_backend.service.EmailService;
import ken.tar.sa_backend.service.SentimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SentimentServiceImpl implements SentimentService {

    private final ClientService clientService;
    private final SentimentRepository sentimentRepository;
    private final AiService aiService;
    private final EmailService emailService;

    @Value("${app.prompt.sentiment}")
    private String sentimentPrompt;
    @Value("${app.prompt.email}")
    private String emailPrompt;
    @Value("${APP_EMAIL}")
    private String appEmail;

    @Autowired
    public SentimentServiceImpl(SentimentRepository sentimentRepository, ClientService clientService, AiService aiService, EmailService emailService) {
        this.sentimentRepository = sentimentRepository;
        this.clientService = clientService;
        this.aiService = aiService;
        this.emailService = emailService;
    }

    @Override
    public void save(Sentiment sentiment) {
        Client client = clientService.readOrCreate(sentiment.getClient());
        sentiment.setClient(client);

        String prompt = sentimentPrompt.formatted(sentiment.getText());
        TypeSentiment chatResponse = TypeSentiment.valueOf(aiService.chat(prompt).toUpperCase());
        sentiment.setSentiment(chatResponse);
        sentimentRepository.save(sentiment);
        notifyAdminAndClient(sentiment);
    }

    private void notifyAdminAndClient(Sentiment sentiment) {
        // Génération de l'email via le service IA
        String prompt = emailPrompt.formatted("Avis utilisateur sur vos services : " + sentiment.getText());
        Email email = aiService.generateEmail(prompt);
        email.setTo(sentiment.getClient().getEmail());
        email.setFrom(appEmail);
        emailService.sendEmail(email);

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
                .orElseThrow(() -> new EntityNotFoundException("Aucun sentiment n'existe avec l'id " + id));
    }
}