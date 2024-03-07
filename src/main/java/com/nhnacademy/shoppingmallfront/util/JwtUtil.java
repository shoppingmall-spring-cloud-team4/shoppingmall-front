package com.nhnacademy.shoppingmallfront.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;

@Slf4j
@Component
public class JwtUtil {
    public static final String JWT_COOKIE = "auth";
    public static final String EXP_HEADER = "X-Expire";
    public static final String TOKEN_TYPE = "Bearer ";
    public static final Long MILL_SEC = 1000L;
    public static final Integer EXPIRE_TIME = 7200;

    public static Cookie makeJwtCookie(String accessToken, Long expireTime) {
        String token = accessToken + "." + expireTime;
        Cookie cookie = new Cookie(JWT_COOKIE, token);

        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(EXPIRE_TIME);

        return cookie;
    }
}
