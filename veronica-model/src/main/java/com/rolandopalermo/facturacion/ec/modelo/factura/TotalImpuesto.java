/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo.factura;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlType;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rolando
 */
@Getter
@Setter
@XmlType(propOrder = { "codigo", "codigoPorcentaje", "baseImponible", "tarifa", "valor" })
public class TotalImpuesto {

	@NotEmpty
	protected String codigo;
	@NotEmpty
	protected String codigoPorcentaje;
	@NotNull
	protected BigDecimal baseImponible;
	protected BigDecimal tarifa;
	@NotNull
	protected BigDecimal valor;

}
