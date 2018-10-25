/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo.retencion;

import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.rolandopalermo.facturacion.ec.modelo.ComprobanteElectronico;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rolando
 */
@Getter
@Setter
@XmlRootElement(name = "comprobanteRetencion")
@XmlType(propOrder = { "id", "version", "infoTributaria", "infoCompRetencion", "impuesto", "campoAdicional" })
public class ComprobanteRetencion extends ComprobanteElectronico {

	protected InfoCompRetencion infoCompRetencion;
	private List<Impuesto> impuesto;

	@XmlElementWrapper(name = "impuestos")
	public List<Impuesto> getImpuesto() {
		return impuesto;
	}

}