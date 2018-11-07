/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo;

import java.math.BigDecimal;

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
@XmlType(propOrder = { "codigo", "codigoPorcentaje", "tarifa", "baseImponible", "valor" })
public class Impuesto {

	@NotEmpty
	protected String codigo;
	@NotEmpty
	protected String codigoPorcentaje;
	protected BigDecimal tarifa;
	protected BigDecimal baseImponible;
	protected BigDecimal valor;

}
