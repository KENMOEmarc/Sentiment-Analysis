package ken.tar.sa_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import ken.tar.sa_backend.enums.TypeSentiment;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "SENTIMENT")
public class Sentiment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TEXTE")
    private String text;

    @Column(name = "TYPE")
    private TypeSentiment  sentiment;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "CLIENT_ID")
    private Client client;

    public Sentiment() {}

    public Sentiment(String text, TypeSentiment sentiment, Client client) {
        this.text = text;
        this.sentiment = sentiment;
        this.client = client;
    }

}
