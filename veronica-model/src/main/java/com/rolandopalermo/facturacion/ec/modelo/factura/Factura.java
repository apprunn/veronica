/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo.factura;

import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import javax.validation.constraints.NotEmpty;

import com.rolandopalermo.facturacion.ec.modelo.ComprobanteElectronico;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rolando
 */
@Getter
@Setter
@XmlRootElement(name = "factura")
@XmlType(propOrder = { "id", "version", "infoTributaria", "infoFactura", "pago", "detalle", "campoAdicional" })
public class Factura extends ComprobanteElectronico {

	@Valid
	protected InfoFactura infoFactura;
	@NotEmpty
	@Valid
	private List<Pago> pago;
	@NotEmpty
	@Valid
	private List<FacturaDetalle> detalle;

	@XmlElementWrapper(name = "detalles")
	public List<FacturaDetalle> getDetalle() {
		return detalle;
	}

	@XmlElementWrapper(name = "pagos")
	public List<Pago> getPago() { return pago; }

}
