/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rolando
 */
@Getter
@Setter
public class CampoAdicional {

	@NotEmpty
	protected String value;
	@NotEmpty
	protected String nombre;

	@XmlAttribute(name = "nombre")
	public String getNombre() {
		return nombre;
	}

	@XmlValue
	public String getValue() {
		return value;
	}

}