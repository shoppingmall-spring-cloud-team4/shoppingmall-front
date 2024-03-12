package com.nhnacademy.shoppingmallfront.service;

import com.nhnacademy.shoppingmallfront.adaptor.AuthAdaptor;
import com.nhnacademy.shoppingmallfront.dto.AuthUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthAdaptor authAdaptor;

    @Override
    public UserDetails loadUserByUsername(String accessToken) throws UsernameNotFoundException {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        HttpServletRequest request = servletRequestAttributes.getRequest();
        HttpServletResponse response = servletRequestAttributes.getResponse();

        AuthUserDto authUserDto = authAdaptor.getUser(accessToken).getBody();

        log.info("{}", authUserDto);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authUserDto.getUserAuth()));

        return new User(
                authUserDto.getUserId(),
                "dummy",
                authorities
        );
    }
}
