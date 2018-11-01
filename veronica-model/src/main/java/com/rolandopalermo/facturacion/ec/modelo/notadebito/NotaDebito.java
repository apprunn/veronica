package com.rolandopalermo.facturacion.ec.modelo.notadebito;

import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.rolandopalermo.facturacion.ec.modelo.ComprobanteElectronico;

import com.rolandopalermo.facturacion.ec.modelo.factura.Pago;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlType(name = "", propOrder = { "id", "version", "infoTributaria", "infoNotaDebito", "pago", "motivo", "campoAdicional" })
@XmlRootElement(name = "notaDebito")
public class NotaDebito extends ComprobanteElectronico {

	protected InfoNotaDebito infoNotaDebito;
	protected List<Pago> pago;
	protected List<Motivo> motivo;

	@XmlElementWrapper(name = "motivos")
	public List<Motivo> getMotivo() {
		return motivo;
	}

	@XmlElementWrapper(name = "pagos")
	public List<Pago> getPago() {
		return pago;
	}
}