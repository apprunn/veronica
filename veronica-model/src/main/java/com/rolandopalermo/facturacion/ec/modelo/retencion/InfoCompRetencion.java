/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo.retencion;

import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rolando
 */
@Getter
@Setter
@XmlType(propOrder = { "fechaEmision", "dirEstablecimiento", "contribuyenteEspecial", "obligadoContabilidad",
		"tipoIdentificacionSujetoRetenido", "razonSocialSujetoRetenido", "identificacionSujetoRetenido",
		"periodoFiscal" })
public class InfoCompRetencion {

	protected String fechaEmision;
	protected String dirEstablecimiento;
	protected String contribuyenteEspecial;
	protected String obligadoContabilidad;
	protected String tipoIdentificacionSujetoRetenido;
	protected String razonSocialSujetoRetenido;
	protected String identificacionSujetoRetenido;
	protected String periodoFiscal;

}