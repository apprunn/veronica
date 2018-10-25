/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo.guia;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rolando
 */
@Getter
@Setter
@XmlRootElement(name = "infoGuiaRemision")
@XmlType(propOrder = { "dirEstablecimiento", "dirPartida", "razonSocialTransportista",
		"tipoIdentificacionTransportista", "rucTransportista", "rise", "obligadoContabilidad", "contribuyenteEspecial",
		"fechaIniTransporte", "fechaFinTransporte", "placa" })
public class InfoGuiaRemision {

	protected String dirEstablecimiento;
	protected String dirPartida;
	protected String razonSocialTransportista;
	protected String tipoIdentificacionTransportista;
	protected String rucTransportista;
	protected String rise;
	protected String obligadoContabilidad;
	protected String contribuyenteEspecial;
	protected String fechaIniTransporte;
	protected String fechaFinTransporte;
	protected String placa;

}