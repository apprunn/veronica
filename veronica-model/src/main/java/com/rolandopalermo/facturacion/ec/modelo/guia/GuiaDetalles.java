/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo.guia;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.rolandopalermo.facturacion.ec.modelo.DetAdicional;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rolando
 */
@Getter
@Setter
@XmlRootElement(name = "detalle")
@XmlType(propOrder = {
    "codigoInterno", "codigoAdicional", "descripcion", "cantidad",
    "detAdicional"})
public class GuiaDetalles {

    protected String codigoInterno;
    protected String codigoAdicional;
    protected String descripcion;
    protected BigDecimal cantidad;
    private List<DetAdicional> detAdicional;
    

}