package com.flipdeal.demo.exception;

import org.springframework.security.core.AuthenticationException;

public class EmailVerificationException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

	public EmailVerificationException(String message) {
        super(message);
    }

}
