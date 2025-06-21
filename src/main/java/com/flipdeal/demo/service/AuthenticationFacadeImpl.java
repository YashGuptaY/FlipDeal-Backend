package com.flipdeal.demo.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthenticationFacadeImpl {

    public Authentication getAuthentication() {
        log.info("AuthenticationFacadeImpl: getAuthentication called");
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
