package ken.tar.sa_backend.service.impl;

import ken.tar.sa_backend.entity.Email;
import ken.tar.sa_backend.service.AiService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;
    private final Executor asyncExecutor;


    public AiServiceImpl(ChatClient.Builder builder, @Qualifier("taskExecutor") Executor asyncExecutor) {
        chatClient = builder.build();
        this.asyncExecutor = asyncExecutor;
    }

    public String chat(String prompt) {
        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }

    public Email generateEmail(String prompt) {
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