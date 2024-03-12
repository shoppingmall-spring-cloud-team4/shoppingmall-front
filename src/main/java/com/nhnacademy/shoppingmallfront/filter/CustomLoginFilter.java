package com.nhnacademy.shoppingmallfront.filter;

import com.nhnacademy.shoppingmallfront.adaptor.AuthAdaptor;
import com.nhnacademy.shoppingmallfront.dto.LoginRequest;
import com.nhnacademy.shoppingmallfront.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthAdaptor authAdaptor;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String id = obtainUsername(request);
        String password = obtainPassword(request);

        LoginRequest loginRequest = new LoginRequest(id, password);
        log.info("{}", loginRequest);
        ResponseEntity<Void> jwtResponse = authAdaptor.loginRequest(loginRequest);

        String accessToken = getAccessToken(jwtResponse);
        Long expireTime = getExpireTime(jwtResponse);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(accessToken, expireTime);

        Cookie cookie = JwtUtil.makeJwtCookie(accessToken, expireTime);

        response.addCookie(cookie);

        return getAuthenticationManager().authenticate(token);
    }

    private static String getAccessToken(ResponseEntity<Void> jwtResponse) {
        return Objects.requireNonNull(jwtResponse.getHeaders()
                .get("Authorization")).get(0).substring(JwtUtil.TOKEN_TYPE.length());
    }

    private static Long getExpireTime(ResponseEntity<Void> jwtResponse) {
        return Long.parseLong(
                Objects.requireNonNull(
                        jwtResponse.getHeaders().get(JwtUtil.EXP_HEADER)).get(0));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        response.sendRedirect("/");
        this.getSuccessHandler().onAuthenticationSuccess(request,response,authResult);
    }
}
