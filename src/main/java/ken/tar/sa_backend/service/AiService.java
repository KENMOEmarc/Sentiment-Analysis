package ken.tar.sa_backend.service;

import ken.tar.sa_backend.entity.Email;

import java.util.concurrent.CompletableFuture;

public interface AiService {
    String chat(String prompt);
    Email generateEmail(String prompt);

    CompletableFuture<String> chatAsync(String prompt);
    CompletableFuture<Email> generateEmailAsync(String prompt);
}