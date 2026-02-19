package ken.tar.sa_backend.repository;

import ken.tar.sa_backend.entity.Sentiment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SentimentRepository extends JpaRepository<Sentiment, Long> {
}
