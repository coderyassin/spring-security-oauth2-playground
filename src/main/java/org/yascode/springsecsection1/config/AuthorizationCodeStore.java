package org.yascode.springsecsection1.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthorizationCodeStore {

    private final Map<String, AuthorizationCode> codes = new ConcurrentHashMap<>();

    public void save(String code, AuthorizationCode authorizationCode) {
        codes.put(code, authorizationCode);
    }

    public AuthorizationCode get(String code) {
        return codes.get(code);
    }

    public AuthorizationCode consume(String code) {
        return codes.remove(code);
    }

    public AuthorizationCode remove(String code) {
        return codes.remove(code);
    }
}
