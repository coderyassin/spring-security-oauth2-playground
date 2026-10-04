package org.yascode.springsecsection1.config.security.web.access;

import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.authorization.event.AuthorizationGrantedEvent;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import java.util.function.Predicate;
import java.util.function.Supplier;

@Setter
public class CustomAuthorizationEventPublisher implements AuthorizationEventPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private Predicate<AuthorizationResult> shouldPublishResult = (result) -> true;

    public CustomAuthorizationEventPublisher(ApplicationEventPublisher eventPublisher) {
        Assert.notNull(eventPublisher, "eventPublisher cannot be null");
        this.eventPublisher = eventPublisher;
    }

    @Override
    public <T> void publishAuthorizationEvent(
            Supplier<Authentication> authentication,
            T object,
            @Nullable AuthorizationResult result
    ) {
        if (result == null) {
            return;
        }
        if (!this.shouldPublishResult.test(result)) {
            return;
        }

        if (result.isGranted()) {
            AuthorizationGrantedEvent<T> success = new AuthorizationGrantedEvent<>(authentication, object, result);
            this.eventPublisher.publishEvent(success);
        }

        AuthorizationDeniedEvent<T> failure = new AuthorizationDeniedEvent<>(authentication, object, result);
        this.eventPublisher.publishEvent(failure);
    }
}
