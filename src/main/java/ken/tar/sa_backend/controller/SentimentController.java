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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
     public void addSentiment(@RequestBody Sentiment sentiment){
        theSentimentService.save(sentiment);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public List<Sentiment> getSentiments(){
        return theSentimentService.getSentiments();
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteSentiment(@PathVariable long id){
        theSentimentService.delete(id);
    }
}
