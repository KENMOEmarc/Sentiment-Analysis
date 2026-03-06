package ken.tar.sa_backend.controller;

import ken.tar.sa_backend.entity.Sentiment;
import ken.tar.sa_backend.service.impl.SentimentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/sentiments")
public class SentimentController {
    private SentimentServiceImpl theSentimentServiceImpl;
    @Autowired
    public SentimentController(SentimentServiceImpl sentimentServiceImpl) {
        theSentimentServiceImpl = sentimentServiceImpl;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Sentiment getSentiment(@PathVariable Long id){
        return theSentimentServiceImpl.getSentiment(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Sentiment> getSentiments(){
        return theSentimentServiceImpl.getSentiments();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
     public void addSentiment(@RequestBody Sentiment sentiment){
        theSentimentServiceImpl.save(sentiment);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteSentiment(@PathVariable long id){
        theSentimentServiceImpl.delete(id);
    }
}
