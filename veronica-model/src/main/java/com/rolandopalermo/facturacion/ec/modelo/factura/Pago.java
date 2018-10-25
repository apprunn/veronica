package com.rolandopalermo.facturacion.ec.modelo.factura;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pago {

	@NotEmpty
	private String formaPago;
	@NotNull
	private BigDecimal total;
	@NotEmpty
	private String plazo;
	@NotEmpty
	private String unidadTiempo;
}
