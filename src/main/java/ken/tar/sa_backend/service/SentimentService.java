package ken.tar.sa_backend.service;

import jakarta.persistence.EntityNotFoundException;
import ken.tar.sa_backend.entity.Client;
import ken.tar.sa_backend.entity.Sentiment;
import ken.tar.sa_backend.enums.TypeSentiment;
import ken.tar.sa_backend.repository.SentimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SentimentService {

    private final ClientService theClientService;
    private final SentimentRepository theSentimentRepository;
    private final AiService theAiService;

    @Autowired
    public SentimentService(SentimentRepository sentimentRepository, ClientService clientService, AiService theAiService) {
        theSentimentRepository = sentimentRepository;
        theClientService = clientService;
        this.theAiService = theAiService;
    }

    public void save(Sentiment sentiment) {
        Client theClient = theClientService.readOrCreate(sentiment.getClient());
        sentiment.setClient(theClient);
        String prompt = """
                Tu es un community manager et tu souhaites analyser le commentaire %s ....
                Et repondre POSITIF ou NEGATIF en fonction de comment le client a trouve les services de l'entreprise.
                """.formatted(sentiment.getText());
        TypeSentiment chatResponse = TypeSentiment.valueOf(theAiService.chat(prompt).toUpperCase());
        sentiment.setSentiment(chatResponse);
        theSentimentRepository.save(sentiment);
    }

    public List<Sentiment> getSentiments() {
        return theSentimentRepository.findAll();
    }

    public void delete(long id) {
        theSentimentRepository.deleteById(id);
    }

    public Sentiment getSetiment(Long id) {
        return theSentimentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Aucun sentiment n'existe avec l'id " + id)
        );
    }
}
