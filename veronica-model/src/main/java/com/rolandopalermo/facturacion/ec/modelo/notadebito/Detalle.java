package com.rolandopalermo.facturacion.ec.modelo.notadebito;

import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlType(name = "detalle", propOrder = { "motivoModificacion" })
public class Detalle {

	protected String motivoModificacion;

}