package com.nhnacademy.shoppingmallfront.controller;

import com.nhnacademy.shoppingmallfront.dto.AuthUserDto;
import com.nhnacademy.shoppingmallfront.dto.LoginRequest;
import com.nhnacademy.shoppingmallfront.util.CookieUtil;
import com.nhnacademy.shoppingmallfront.util.JwtUtil;
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

import static com.nhnacademy.shoppingmallfront.util.JwtUtil.EXP_HEADER;


@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final RestTemplate restTemplate;

    @PostMapping("/login")
    public String doLogin(LoginRequest loginRequest, HttpServletResponse response) {
        ResponseEntity<Void> exchange = restTemplate.postForEntity(
                "http://localhost:8000/login",
                loginRequest,
                Void.class
        );

        String accessToken = exchange.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0).substring(7);
        Long expireTime = Long.parseLong(exchange.getHeaders().get(EXP_HEADER).get(0));

        Cookie cookie = JwtUtil.makeJwtCookie(accessToken, expireTime);


        response.addCookie(cookie);
        return "redirect:/";
    }

    @GetMapping("/api/account/users")
    public String getStudent(HttpServletRequest request, HttpServletResponse response) {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        HttpHeaders headers = new HttpHeaders();
        log.info(">->->->{}", request.getAttribute(HttpHeaders.AUTHORIZATION));
        headers.add(HttpHeaders.AUTHORIZATION, request.getAttribute(HttpHeaders.AUTHORIZATION).toString());
        Cookie cookie = CookieUtil.findCookie("auth");
        ResponseEntity<AuthUserDto> exchange = restTemplate.exchange(
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
