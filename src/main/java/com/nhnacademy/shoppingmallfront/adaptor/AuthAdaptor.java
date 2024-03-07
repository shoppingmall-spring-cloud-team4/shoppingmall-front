package com.nhnacademy.shoppingmallfront.adaptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthAdaptor {
    private final RestTemplate restTemplate;

    public ResponseEntity<Void> getRefreshToken(String accessToken){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION,accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                "http://localhost:8100/auth/refresh",
                HttpMethod.POST,
                entity,
                Void.class);
    }
}
