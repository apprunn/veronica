/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo.factura;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.rolandopalermo.facturacion.ec.modelo.DetAdicional;
import com.rolandopalermo.facturacion.ec.modelo.Impuesto;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rolando
 */
@Getter
@Setter
@XmlRootElement(name = "detalle")
@XmlType(propOrder = { "codigoPrincipal", "codigoAuxiliar", "descripcion", "cantidad", "precioUnitario", "descuento",
		"precioTotalSinImpuesto", "detAdicional", "impuesto" })
public class FacturaDetalle {

	protected String codigoPrincipal;
	protected String codigoAuxiliar;
	@NotEmpty
	protected String descripcion;
	protected BigDecimal cantidad;
	protected BigDecimal precioUnitario;
	protected BigDecimal descuento;
	protected BigDecimal precioTotalSinImpuesto;
	private List<DetAdicional> detAdicional;
	@NotEmpty
	@Valid
	private List<Impuesto> impuesto;

	@XmlElementWrapper(name = "detallesAdicionales")
	public List<DetAdicional> getDetAdicional() {
		return detAdicional;
	}

	@XmlElementWrapper(name = "impuestos")
	public List<Impuesto> getImpuesto() {
		return impuesto;
	}

}
