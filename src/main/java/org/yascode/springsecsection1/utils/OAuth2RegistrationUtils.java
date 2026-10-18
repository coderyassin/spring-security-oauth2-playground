package org.yascode.springsecsection1.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2RegistrationUtils {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public ClientRegistration getRegistration(String registrationId) {
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(registrationId);

        if (registration == null) {
            throw new IllegalArgumentException(
                    "Unknown OAuth2 registrationId: " + registrationId
            );
        }

        return registration;
    }

    public String getProvider(String registrationId) {
        return getRegistration(registrationId)
                .getProviderDetails()
                .getIssuerUri();
    }

    public String getClientId(String registrationId) {
        return getRegistration(registrationId).getClientId();
    }

    public String getClientSecret(String registrationId) {
        return getRegistration(registrationId).getClientSecret();
    }
}
