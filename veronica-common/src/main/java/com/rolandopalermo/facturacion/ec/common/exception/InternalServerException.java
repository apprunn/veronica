package com.rolandopalermo.facturacion.ec.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InternalServerException() {
	}

	public InternalServerException(String message) {
		super(message);
	}

	public InternalServerException(String message, Throwable cause) {
		super(message, cause);
	}
}