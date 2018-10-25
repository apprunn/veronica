package com.rolandopalermo.facturacion.ec.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenericResponse<T> {

	private T contenido;

}