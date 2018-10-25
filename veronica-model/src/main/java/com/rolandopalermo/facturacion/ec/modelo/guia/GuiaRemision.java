/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo.guia;

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
@XmlRootElement(name = "guiaRemision")
@XmlType(propOrder = { "id", "version", "infoTributaria", "infoGuiaRemision", "destinatario", "campoAdicional" })
public class GuiaRemision extends ComprobanteElectronico {

	protected InfoGuiaRemision infoGuiaRemision;
	private List<Destinatario> destinatario;

	@XmlElementWrapper(name = "destinatarios")
	public List<Destinatario> getDestinatario() {
		return destinatario;
	}

}