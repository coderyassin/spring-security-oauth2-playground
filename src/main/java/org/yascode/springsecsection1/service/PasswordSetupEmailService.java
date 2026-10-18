package org.yascode.springsecsection1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PasswordSetupEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendPasswordSetupEmail(
            String email,
            String code
    ) {
        String link = UriComponentsBuilder
                .fromUriString(frontendUrl)
                .path("/set-password")
                .queryParam("code", code)
                .build()
                .toUriString();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Set up your password");
        message.setText("""
                Hello,

                Your account has been created.
                Click the following link to set up your password:

                %s

                This link expires in 15 minutes.
                If you did not make this request, ignore this email.
                """.formatted(link));

        mailSender.send(message);
    }
}
