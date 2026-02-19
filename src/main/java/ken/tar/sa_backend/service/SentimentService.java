package ken.tar.sa_backend.service;

import ken.tar.sa_backend.entity.Client;
import ken.tar.sa_backend.entity.Sentiment;
import ken.tar.sa_backend.repository.SentimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SentimentService {

    private ClientService theClientService;
    private SentimentRepository theSentimentRepository;

    @Autowired
    public SentimentService(SentimentRepository sentimentRepository,  ClientService clientService) {
        theSentimentRepository = sentimentRepository;
        theClientService = clientService;
    }

    public void save(Sentiment sentiment) {
        Client theClient = theClientService.readOrCreate(sentiment.getClient());
        sentiment.setClient(theClient);
        theSentimentRepository.save(sentiment);
    }

    public List<Sentiment> getSentiments() {
        return theSentimentRepository.findAll();
    }

    public void delete(long id) {
        theSentimentRepository.deleteById(id);
    }
}
