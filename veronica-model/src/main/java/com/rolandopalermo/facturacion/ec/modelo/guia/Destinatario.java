/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo.guia;

import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
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
@XmlRootElement(name = "destinatario")
@XmlType(propOrder = {
    "identificacionDestinatario", "razonSocialDestinatario", "dirDestinatario",
    "motivoTraslado", "docAduaneroUnico", "codEstabDestino",
    "ruta", "codDocSustento", "numDocSustento",
    "numAutDocSustento", "fechaEmisionDocSustento", "detalle"})
public class Destinatario {

    protected String identificacionDestinatario;
    protected String razonSocialDestinatario;
    protected String dirDestinatario;
    protected String motivoTraslado;
    protected String docAduaneroUnico;
    protected String codEstabDestino;
    protected String ruta;
    protected String codDocSustento;
    protected String numDocSustento;
    protected String numAutDocSustento;
    protected String fechaEmisionDocSustento;
    private List<GuiaDetalles> detalle;

    @XmlElementWrapper(name="detalles")
    public List<GuiaDetalles> getDetalle() {
        return detalle;
    }

}