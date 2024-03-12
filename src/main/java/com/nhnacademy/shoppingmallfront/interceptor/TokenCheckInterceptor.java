package com.nhnacademy.shoppingmallfront.interceptor;

import com.nhnacademy.shoppingmallfront.adaptor.AuthAdaptor;
import com.nhnacademy.shoppingmallfront.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;

import static com.nhnacademy.shoppingmallfront.util.JwtUtil.JWT_COOKIE;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCheckInterceptor implements HandlerInterceptor {
    private final AuthAdaptor authAdaptor;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie jwtCookie = CookieUtil.findCookie(JWT_COOKIE);
        String cookieValue = Objects.requireNonNull(jwtCookie).getValue();
        String exp = cookieValue.split("\\.")[3];
        String accessToken = jwtCookie.getValue().substring(0, getTokenLength(cookieValue, exp));

        Long validTime = getValidTime(exp);

        if (validTime <= 0) {
            ResponseEntity<Void> result = authAdaptor.getRefreshToken(accessToken);
            return tokenReissue(request, response, jwtCookie, result);
        }
        requestContainAccessToken(request, accessToken);
        return true;
    }

    private static Integer getTokenLength(String cookieValue, String exp) {
        return cookieValue.length() - (exp.length() + 1);
    }

    private static Long getValidTime(String exp) {
        Long now = new Date().getTime();
        return (Long.parseLong(exp) - now) / 1000L;
    }

    private static boolean tokenReissue(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Cookie jwtCookie, ResponseEntity<Void> result) {

        String reissueToken = getReissueToken(result);
        Long expireTime = getExpireTime(result);

        setCookie(response, jwtCookie, reissueToken, expireTime);
        requestContainAccessToken(request, reissueToken);
        return true;
    }

    private static String getReissueToken(ResponseEntity<Void> result) {
        return Objects.requireNonNull(result.getHeaders().get("Authorization").get(0).substring("Bearer ".length()));
    }

    private static Long getExpireTime(ResponseEntity<Void> result) {
        return Long.parseLong(Objects.requireNonNull(result.getHeaders().get("X-Expire")).get(0));
    }
    private static void setCookie(HttpServletResponse response, Cookie jwtCookie,
                                  String newAccessToken, Long expireTime) {
        jwtCookie.setValue(newAccessToken + "." + expireTime);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(7200);
        response.addCookie(jwtCookie);
    }

    private static void requestContainAccessToken(HttpServletRequest request, String accessToken) {
        request.setAttribute(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    }
}
