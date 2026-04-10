package ken.tar.sa_backend.controller;

import ken.tar.sa_backend.entity.Sentiment;
import ken.tar.sa_backend.service.SentimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/sentiments")
public class SentimentController {
    private SentimentService theSentimentService;
    @Autowired
    public SentimentController(SentimentService sentimentService) {
        theSentimentService = sentimentService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Sentiment getSentiment(@PathVariable Long id){
        return theSentimentService.getSentiment(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Sentiment> getSentiments(){
        return theSentimentService.getSentiments();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
     public void addSentiment(@RequestBody Sentiment sentiment){
        // also just in case they pass an id in JSON ... set id to 0
        // this is to force a save of new item ... instead of update
        sentiment.setId(0L);
        theSentimentService.save(sentiment);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteSentiment(@PathVariable long id){
        theSentimentService.delete(id);
    }
}
