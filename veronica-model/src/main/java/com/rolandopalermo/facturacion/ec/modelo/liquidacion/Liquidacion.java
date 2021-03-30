package com.rolandopalermo.facturacion.ec.modelo.liquidacion;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.rolandopalermo.facturacion.ec.modelo.ComprobanteElectronico;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "liquidacionCompra")
@XmlType(propOrder = { "id", "version", "infoTributaria", "infoLiquidacionCompra", "detalle", "campoAdicional" })
public class Liquidacion extends ComprobanteElectronico {
    

	@Valid
	protected InfoLiquidacion infoLiquidacionCompra;
	@NotEmpty
	@Valid
	private List<LiquidacionDetalle> detalle;

	@XmlElementWrapper(name = "detalles")
	public List<LiquidacionDetalle> getDetalle() {
		return detalle;
	}

}
