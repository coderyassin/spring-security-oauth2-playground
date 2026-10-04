package org.yascode.springsecsection1.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "app.security.login")
public class LoginSecurityProperties {

    private int maxFailedAttempts = 5;

    private Duration lockDuration = Duration.ofMinutes(5);

    private Duration failedAttemptWindow = Duration.ofHours(24);

    public int getMaxFailedAttempts() {
        return maxFailedAttempts;
    }

    public void setMaxFailedAttempts(int maxFailedAttempts) {
        this.maxFailedAttempts = maxFailedAttempts;
    }

    public Duration getLockDuration() {
        return lockDuration;
    }

    public void setLockDuration(Duration lockDuration) {
        this.lockDuration = lockDuration;
    }

    public Duration getFailedAttemptWindow() {
        return failedAttemptWindow;
    }

    public void setFailedAttemptWindow(Duration failedAttemptWindow) {
        this.failedAttemptWindow = failedAttemptWindow;
    }
}
