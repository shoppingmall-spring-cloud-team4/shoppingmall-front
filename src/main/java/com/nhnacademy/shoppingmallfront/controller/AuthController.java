package com.nhnacademy.shoppingmallfront.controller;

import com.nhnacademy.shoppingmallfront.dto.LoginRequest;
import com.nhnacademy.shoppingmallfront.dto.UserResponseDto;
import com.nhnacademy.shoppingmallfront.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final RestTemplate restTemplate;

    @PostMapping("/login")
    public String doLogin(LoginRequest loginRequest, HttpServletResponse response) {
        log.info("{}", loginRequest);
        ResponseEntity<Void> exchange = restTemplate.postForEntity(
                "http://localhost:8000/login",
                loginRequest,
                Void.class
        );
        Cookie cookie = new Cookie("auth", exchange.getHeaders().get("Authorization").get(0).substring(7));
        cookie.setMaxAge(259200);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
        return "redirect:/";
    }

    @GetMapping("/api/account/users")
    public String getStudent(HttpServletRequest request, HttpServletResponse response) {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        HttpHeaders headers = new HttpHeaders();
        Cookie cookie = CookieUtil.findCookie("auth");
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + cookie.getValue());
        headers.add("withCredentials","true");
        log.info("{}", cookie.getValue());
        ResponseEntity<UserResponseDto> exchange = restTemplate.exchange(
                "http://localhost:8000/api/account/users",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );


        log.info("{}", exchange.getBody());
        log.info("{}", exchange.getHeaders());
        return "login";
    }
}
