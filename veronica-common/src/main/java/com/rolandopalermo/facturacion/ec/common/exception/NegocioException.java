package com.rolandopalermo.facturacion.ec.common.exception;

import lombok.Getter;

@Getter
public class NegocioException extends Exception {

	private static final long serialVersionUID = 4956405604792374198L;

	private int code;

	public NegocioException(String message) {
		super(message);
	}

	public NegocioException(String message, int code) {
		super(message);
		this.code = code;
	}
}