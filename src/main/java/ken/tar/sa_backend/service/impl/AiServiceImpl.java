package ken.tar.sa_backend.service.impl;

import ken.tar.sa_backend.entity.Email;
import ken.tar.sa_backend.service.AiService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;

    public AiServiceImpl(ChatClient.Builder builder) {
        chatClient = builder.build();
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