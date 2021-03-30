package com.rolandopalermo.facturacion.ec.modelo.liquidacion;

import java.math.BigDecimal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "pago")
@XmlType(propOrder = { "formaPago", "total", "plazo", "unidadTiempo" })
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
