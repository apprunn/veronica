/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo.notacredito;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rolando
 */
@Getter
@Setter
@XmlType(propOrder = {
    "fechaEmision", "dirEstablecimiento", "tipoIdentificacionComprador", "razonSocialComprador",
    "identificacionComprador", "contribuyenteEspecial", "obligadoContabilidad", "rise", "codDocModificado",
    "numDocModificado", "fechaEmisionDocSustento", "totalSinImpuestos", "valorModificacion", "moneda", "totalImpuesto", "motivo"})
public class InfoNotaCredito {

    protected String fechaEmision;
    protected String dirEstablecimiento;
    protected String tipoIdentificacionComprador;
    protected String razonSocialComprador;
    protected String identificacionComprador;
    protected String contribuyenteEspecial;
    protected String obligadoContabilidad;
    protected String rise;
    protected String codDocModificado;
    protected String numDocModificado;
    protected String fechaEmisionDocSustento;
    protected BigDecimal totalSinImpuestos;
    protected BigDecimal valorModificacion;
    protected String moneda;
    protected List<TotalImpuesto> totalImpuesto;
    protected String motivo;

    @XmlElementWrapper(name="totalConImpuestos")
    public List<TotalImpuesto> getTotalImpuesto() {
        return totalImpuesto;
    }
}
