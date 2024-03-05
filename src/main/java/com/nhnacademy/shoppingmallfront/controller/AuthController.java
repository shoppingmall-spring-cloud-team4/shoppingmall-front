package com.nhnacademy.shoppingmallfront.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.shoppingmallfront.dto.LoginRequest;
import com.nhnacademy.shoppingmallfront.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final RestTemplate restTemplate;

    @PostMapping("/login")
    public String doLogin(LoginRequest loginRequest, HttpServletResponse response) {
        log.info("{}", loginRequest);
        ResponseEntity<String> exchange = restTemplate.postForEntity(
                "http://localhost:8000/login",
                loginRequest,
                String.class
        );
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TokenResponse tokenResponse = objectMapper.readValue(exchange.getBody(), TokenResponse.class);
            log.info("{}", tokenResponse);
            Cookie cookie = new Cookie("access_token", tokenResponse.getAccessToken());
            cookie.setMaxAge(259200);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/";
    }
}
