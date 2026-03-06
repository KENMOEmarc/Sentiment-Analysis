package ken.tar.sa_backend.service;

import ken.tar.sa_backend.entity.Sentiment;
import java.util.List;

public interface SentimentService {
    void save(Sentiment sentiment);
    List<Sentiment> getSentiments();
    void delete(long id);
    Sentiment getSentiment(Long id);
}