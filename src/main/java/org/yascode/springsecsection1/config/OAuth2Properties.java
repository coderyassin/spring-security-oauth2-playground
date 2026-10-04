package org.yascode.springsecsection1.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.oauth2")
public class OAuth2Properties {

    private List<String> authorizedRedirectUris = new ArrayList<>();

    public List<String> getAuthorizedRedirectUris() {
        return authorizedRedirectUris;
    }

    public void setAuthorizedRedirectUris(
            List<String> authorizedRedirectUris) {
        this.authorizedRedirectUris = authorizedRedirectUris;
    }
}
