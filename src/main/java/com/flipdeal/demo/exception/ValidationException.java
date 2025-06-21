package com.flipdeal.demo.exception;

import static com.flipdeal.demo.exception.ProblemDetailExt.forStatusDetailAndErrors;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

public class ValidationException extends ErrorResponseException {

    private static final long serialVersionUID = 1L;

	public ValidationException(final HttpStatus status, final Map<String, List<String>> errors) {
        super(status, forStatusDetailAndErrors(status, "Request validation failed", errors), null);
    }

}