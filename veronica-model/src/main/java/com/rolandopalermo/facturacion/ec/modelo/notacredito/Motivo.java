/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo.notacredito;

import javax.xml.bind.annotation.XmlValue;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rolando
 */
@Getter
@Setter
public class Motivo {

    private String motivo;

    @XmlValue
    public String getMotivo() {
        return motivo;
    }

}
