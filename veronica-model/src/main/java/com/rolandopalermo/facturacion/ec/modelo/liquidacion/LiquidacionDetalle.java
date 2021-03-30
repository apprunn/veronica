package com.rolandopalermo.facturacion.ec.modelo.liquidacion;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import javax.validation.constraints.NotEmpty;

import com.rolandopalermo.facturacion.ec.modelo.Impuesto;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rolando
 */
@Getter
@Setter
@XmlRootElement(name = "detalle")
@XmlType(propOrder = { "codigoPrincipal", "descripcion", "cantidad", "precioUnitario", "descuento",
		"precioTotalSinImpuesto", "impuesto" })
public class LiquidacionDetalle {

	protected String codigoPrincipal;
	@NotEmpty
	protected String descripcion;
	protected BigDecimal cantidad;
	protected BigDecimal precioUnitario;
	protected BigDecimal descuento;
	protected BigDecimal precioTotalSinImpuesto;
	@NotEmpty
	@Valid
	private List<Impuesto> impuesto;

	@XmlElementWrapper(name = "impuestos")
	public List<Impuesto> getImpuesto() {
		return impuesto;
	}

}
