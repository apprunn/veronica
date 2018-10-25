/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo.retencion;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rolando
 */
@Getter
@Setter
@XmlType(propOrder = { "codigo", "codigoRetencion", "baseImponible", "porcentajeRetener", "valorRetenido",
		"codDocSustento", "numDocSustento", "fechaEmisionDocSustento" })
public class Impuesto {

	protected String codigo;
	protected String codigoRetencion;
	protected BigDecimal baseImponible;
	protected BigDecimal porcentajeRetener;
	protected BigDecimal valorRetenido;
	protected String codDocSustento;
	protected String numDocSustento;
	protected String fechaEmisionDocSustento;

}