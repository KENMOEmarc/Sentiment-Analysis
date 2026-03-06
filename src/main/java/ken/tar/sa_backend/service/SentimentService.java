package ken.tar.sa_backend.service;

import jakarta.persistence.EntityNotFoundException;
import ken.tar.sa_backend.entity.Client;
import ken.tar.sa_backend.entity.Email;
import ken.tar.sa_backend.entity.Sentiment;
import ken.tar.sa_backend.enums.TypeSentiment;
import ken.tar.sa_backend.repository.SentimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SentimentService {

    private final ClientService theClientService;
    private final SentimentRepository theSentimentRepository;
    private final AiService theAiService;
    private final EmailService theEmailService;

    @Value("${app.prompt.sentiment}")
    private String sentimentPrompt;
    @Value("${app.prompt.email}")
    private String emailPrompt;
    @Value("${APP_EMAIL}")
    private String appEmail;

    @Autowired
    public SentimentService(SentimentRepository sentimentRepository, ClientService clientService, AiService aiService, EmailService emailService) {
        theSentimentRepository = sentimentRepository;
        theClientService = clientService;
        theAiService = aiService;
        theEmailService = emailService;
    }

    public void save(Sentiment sentiment) {
        Client theClient = theClientService.readOrCreate(sentiment.getClient());
        sentiment.setClient(theClient);

        String prompt = sentimentPrompt.formatted(sentiment.getText());
        TypeSentiment chatResponse = TypeSentiment.valueOf(theAiService.chat(prompt).toUpperCase());
        sentiment.setSentiment(chatResponse);
        theSentimentRepository.save(sentiment);
        notifyAdminAndClient(sentiment);
    }

    private void notifyAdminAndClient(Sentiment sentiment) {
        String prompt = emailPrompt.formatted("Avis utilisateur sur vos services : " + sentiment.getText());
        Email email = theAiService.getChatClient().prompt()
                .user(prompt)
                .call()
                .entity(Email.class);

        email.setTo(sentiment.getClient().getEmail());
        email.setFrom(appEmail);
        theEmailService.sendEmail(email);

        String subject = "Customer service compliance";
        String body;
        
        if (sentiment.getSentiment() == TypeSentiment.NEGATIF){
             body = """
                    Nous vous exprimons nos sinceres regrets et prometons de faire mieux la prochaine fois.
                    Votre avis a ete bien pris en compte, surtout merci de votre franchise.
                    """;
        } else {
             body = """
                    Merci de votre retour, nous esperont faire encore mieux la prochaine fois.
                    """;
        }

        Email clientEmail = new Email(appEmail, sentiment.getClient().getEmail(), subject, body);

        theEmailService.sendEmail(clientEmail);
    }

    public List<Sentiment> getSentiments() {
        return theSentimentRepository.findAll();
    }

    public void delete(long id) {
        theSentimentRepository.deleteById(id);
    }

    public Sentiment getSentiment(Long id) {
        return theSentimentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Aucun sentiment n'existe avec l'id " + id)
        );
    }
}
