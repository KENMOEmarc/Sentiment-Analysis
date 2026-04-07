package ken.tar.sa_backend.service;

import ken.tar.sa_backend.entity.Email;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<Boolean> sendEmail(Email emailDetails);
}