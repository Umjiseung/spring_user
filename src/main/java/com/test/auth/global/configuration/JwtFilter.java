package com.test.auth.global.configuration;

import com.test.auth.domain.service.UserService;
import com.test.auth.global.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        final String authorization = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization : {}",authorization);

        // token이 안 보내지면 Block
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            log.error("authorization을 잘못 보냈습니다.");
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }

        // Token 꺼내기
        String token = authorization.replace("Bearer ","");

        // Token Expired되었는지 여부
        if (JwtUtil.isExpired(token, secretKey)) {
            log.error("Token이 만료 되었습니다.");
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }

        // userName Token에서 꺼내기
        String userName = JwtUtil.getUserName(token, secretKey);
        log.info("userName: {}",userName);

        // 권한 부여
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority("USER")));

        // Detail을 넣어주기

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(httpServletRequest,httpServletResponse);

    }



}
