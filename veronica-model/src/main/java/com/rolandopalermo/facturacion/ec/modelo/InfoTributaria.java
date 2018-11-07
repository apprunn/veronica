/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo;

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
@XmlType(propOrder = { "ambiente", "tipoEmision", "razonSocial", "nombreComercial", "ruc", "claveAcceso", "codDoc",
		"estab", "ptoEmi", "secuencial", "dirMatriz" })
public class InfoTributaria {

	@NotEmpty
	protected String ambiente;
	@NotEmpty
	protected String tipoEmision;
	@NotEmpty
	protected String razonSocial;
	protected String nombreComercial;
	@NotEmpty
	protected String ruc;
	protected String claveAcceso;
	@NotEmpty
	protected String codDoc;
	@NotEmpty
	protected String estab;
	@NotEmpty
	protected String ptoEmi;
	@NotEmpty
	protected String secuencial;
	@NotEmpty
	protected String dirMatriz;

}
