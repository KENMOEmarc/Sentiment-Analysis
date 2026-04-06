package ken.tar.sa_backend.service.impl;

import ken.tar.sa_backend.entity.Email;
import ken.tar.sa_backend.service.AiService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

@Service
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;
    private final Executor asyncExecutor;


    public AiServiceImpl(ChatClient.Builder builder, @Qualifier("aiTaskExecutor") Executor asyncExecutor) {
        chatClient = builder.build();
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public String chat(String prompt) {
        return chatClient
                .prompt(prompt)
                .call()
                .content();

    }

    @Override
    public Email generateEmail(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(Email.class);
    }

    public ChatClient getChatClient() {
        return chatClient;
    }
}