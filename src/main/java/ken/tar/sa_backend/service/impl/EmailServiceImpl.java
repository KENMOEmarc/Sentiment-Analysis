package ken.tar.sa_backend.service.impl;

import ken.tar.sa_backend.entity.Email;
import ken.tar.sa_backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public String sendEmail(Email emailDetails) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailDetails.getFrom());
            message.setTo(emailDetails.getTo());
            message.setSubject(emailDetails.getSubject());
            message.setText(emailDetails.getBody());
            mailSender.send(message);
            return "Email sent successfully.";
        } catch (Exception e) {
            return "An error occurred while sending an email: " + e.getMessage();
        }
    }
}
