package com.nhnacademy.shoppingmallfront.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;

public class CookieUtil {

    public static Cookie findCookie(String cookieName) {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();

        if (Objects.isNull(request.getCookies())) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .findAny()
                .orElse(null);
    }

    public static void deleteCookie(HttpServletResponse response, String key) {
        Cookie cookie = findCookie(key);
        Objects.requireNonNull(cookie).setMaxAge(0);
        response.addCookie(cookie);
    }
}
