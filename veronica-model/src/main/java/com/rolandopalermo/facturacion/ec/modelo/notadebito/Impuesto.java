package com.rolandopalermo.facturacion.ec.modelo.notadebito;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlType(name = "impuesto", propOrder = { "codigo", "codigoPorcentaje", "tarifa", "baseImponible", "valor" })
public class Impuesto {

	protected String codigo;
	protected String codigoPorcentaje;
	protected BigDecimal tarifa;
	protected BigDecimal baseImponible;
	protected BigDecimal valor;

}