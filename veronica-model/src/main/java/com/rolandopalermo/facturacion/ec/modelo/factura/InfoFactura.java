/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo.factura;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElementWrapper;
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
@XmlType(propOrder = { "fechaEmision", "dirEstablecimiento", "contribuyenteEspecial", "obligadoContabilidad",
		"tipoIdentificacionComprador", "guiaRemision", "razonSocialComprador", "identificacionComprador", "direccionComprador",
		"totalSinImpuestos", "totalSubsidio", "totalDescuento", "totalImpuesto", "propina", "importeTotal", "moneda", "pago",
		"valorRetIva", "valorRetRenta" })
public class InfoFactura {

	@NotEmpty
	protected String fechaEmision;
	@NotEmpty
	protected String dirEstablecimiento;
	protected String contribuyenteEspecial;
	protected String obligadoContabilidad;
	@NotEmpty
	protected String tipoIdentificacionComprador;
	protected String guiaRemision;
	@NotEmpty
	protected String razonSocialComprador;
	protected String identificacionComprador;
	protected String direccionComprador;
	@NotNull
	protected BigDecimal totalSinImpuestos;
	protected BigDecimal totalSubsidio;
	protected BigDecimal totalDescuento;
	@NotEmpty
	@Valid
	private List<TotalImpuesto> totalImpuesto;
	protected BigDecimal propina;
	@NotNull
	protected BigDecimal importeTotal;
	protected String moneda;
	@NotEmpty
	@Valid
	private List<Pago> pago;
	private BigDecimal valorRetIva;
	private BigDecimal valorRetRenta;

	@XmlElementWrapper(name = "totalConImpuestos")
	public List<TotalImpuesto> getTotalImpuesto() {
		return totalImpuesto;
	}

	@XmlElementWrapper(name = "pagos")
	public List<Pago> getPago() {
	return pago;
	}

}
