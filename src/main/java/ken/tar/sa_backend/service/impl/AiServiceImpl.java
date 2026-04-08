package ken.tar.sa_backend.service.impl;

import ken.tar.sa_backend.config.LoggerFactory;
import ken.tar.sa_backend.entity.Email;
import ken.tar.sa_backend.service.AiService;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class AiServiceImpl implements AiService {

    private final Logger logger;
    private final ChatClient chatClient;
    private final Executor asyncExecutor;


    public AiServiceImpl(LoggerFactory loggerFactory, ChatClient.Builder builder, @Qualifier("taskExecutor") Executor asyncExecutor) {
        this.logger = loggerFactory.getLogger(AiServiceImpl.class);
        chatClient = builder.build();
        this.asyncExecutor = asyncExecutor;
    }

    public String chat(String prompt) {
        logger.info("Chatting with AI about the prompt: {}", prompt);
        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }

    public Email generateEmail(String prompt) {
        logger.info("Generating email with AI about the prompt: {}", prompt);
        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(Email.class);
    }

    @Override
    public CompletableFuture<String> chatAsync(String prompt) {
        return CompletableFuture.supplyAsync(() -> chat(prompt), asyncExecutor);
    }

    @Override
    public CompletableFuture<Email> generateEmailAsync(String prompt) {
        return CompletableFuture.supplyAsync( () -> generateEmail(prompt), asyncExecutor);
    }

    public ChatClient getChatClient() {
        return chatClient;
    }
}