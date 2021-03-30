package com.rolandopalermo.facturacion.ec.modelo.liquidacion;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rolando
 */
@Getter
@Setter
@XmlType(propOrder = { "fechaEmision", "dirEstablecimiento", "tipoIdentificacionProveedor", "razonSocialProveedor", "identificacionProveedor", 
                        "totalSinImpuestos", "totalDescuento", "totalImpuesto", "importeTotal", "moneda", "pago" })
public class InfoLiquidacion {

	@NotEmpty
	protected String fechaEmision;
	@NotEmpty
	protected String dirEstablecimiento;
	@NotEmpty
	protected String tipoIdentificacionProveedor;
	@NotEmpty
	protected String razonSocialProveedor;
	protected String identificacionProveedor;
	@NotNull
	protected BigDecimal totalSinImpuestos;
	protected BigDecimal totalDescuento;
	@NotEmpty
	@Valid
	private List<TotalImpuesto> totalImpuesto;
	@NotNull
	protected BigDecimal importeTotal;
	protected String moneda;
	@NotEmpty
	@Valid
	private List<Pago> pago;

	@XmlElementWrapper(name = "totalConImpuestos")
	public List<TotalImpuesto> getTotalImpuesto() {
		return totalImpuesto;
	}

	@XmlElementWrapper(name = "pagos")
	public List<Pago> getPago() {
	    return pago;
	}

}
