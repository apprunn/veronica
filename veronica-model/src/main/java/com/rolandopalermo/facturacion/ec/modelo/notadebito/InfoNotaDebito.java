/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rolandopalermo.facturacion.ec.modelo.notadebito;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.rolandopalermo.facturacion.ec.modelo.factura.Pago;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rolando
 */
@Getter
@Setter
@XmlType(name = "", propOrder = {"fechaEmision", "dirEstablecimiento", "tipoIdentificacionComprador", "razonSocialComprador", "identificacionComprador", "contribuyenteEspecial", "obligadoContabilidad", "rise", "codDocModificado", "numDocModificado", "fechaEmisionDocSustento", "totalSinImpuestos", "impuesto", "valorTotal", "pago"})
public class InfoNotaDebito {

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
    protected List<Impuesto> impuesto;
    protected BigDecimal valorTotal;
	protected List<Pago> pago;

    @XmlElementWrapper(name = "impuestos")
    public List<Impuesto> getImpuesto() {
        return impuesto;
    }

	@XmlElementWrapper(name = "pagos")
	public List<Pago> getPago() {
	return pago;
	}
}