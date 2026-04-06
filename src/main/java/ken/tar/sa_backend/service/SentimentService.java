package ken.tar.sa_backend.service;

import ken.tar.sa_backend.entity.Sentiment;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SentimentService {
    CompletableFuture<Void> save(Sentiment sentiment);   // now async
    List<Sentiment> getSentiments();
    void delete(long id);
    Sentiment getSentiment(Long id);
    }