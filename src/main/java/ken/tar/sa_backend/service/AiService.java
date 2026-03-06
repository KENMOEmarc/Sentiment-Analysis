package ken.tar.sa_backend.service;

import ken.tar.sa_backend.entity.Email;

public interface AiService {
    String chat(String prompt);
    Email generateEmail(String prompt);
}