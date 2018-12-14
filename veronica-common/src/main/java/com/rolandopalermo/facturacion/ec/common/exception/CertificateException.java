package com.rolandopalermo.facturacion.ec.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertificateException extends RuntimeException {

    private static final long serialVersionUID = 4956405604792374199L;
	private int code;

	public CertificateException(String message) {
		super(message);
	}

	public CertificateException(String message, int code) {
		super(message);
		this.code = code;
	}

}