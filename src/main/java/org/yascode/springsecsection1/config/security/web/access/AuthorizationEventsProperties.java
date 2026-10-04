package org.yascode.springsecsection1.config.security.web.access;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security.authorization.events")
@Setter
public class AuthorizationEventsProperties {

    private boolean publishGranted = false;
    private boolean publishDenied = true;

    public boolean isPublishGranted() {
        return publishGranted;
    }

    public boolean isPublishDenied() {
        return publishDenied;
    }
}

