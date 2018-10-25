package com.rolandopalermo.facturacion.ec.dto.error;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationErrorDTO {

	private String code;
	private String message;

}
