package ken.tar.sa_backend.service;

import ken.tar.sa_backend.entity.Email;

public interface EmailService {
    String sendEmail(Email emailDetails);
}