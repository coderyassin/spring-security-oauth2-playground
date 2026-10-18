package org.yascode.springsecsection1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yascode.springsecsection1.config.PasswordSetupCodeGenerator;
import org.yascode.springsecsection1.utils.HashUtils;

@Service
@RequiredArgsConstructor
public class PasswordSetupLinkService {

    private final PasswordSetupEmailService passwordSetupEmailService;
    private final PasswordSetupTokenService passwordSetupTokenService;
    private final PasswordSetupCodeGenerator codeGenerator;

    public void sendPasswordSetupLink(
            String userId,
            String email
    ) {
        String code = codeGenerator.generate();
        String tokenHash = HashUtils.hashCode(code);

        passwordSetupTokenService.saveToken(userId, tokenHash);

        passwordSetupEmailService.sendPasswordSetupEmail(
                email,
                code
        );
    }


}
