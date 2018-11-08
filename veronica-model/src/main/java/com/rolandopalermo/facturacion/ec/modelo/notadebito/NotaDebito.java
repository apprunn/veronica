package com.rolandopalermo.facturacion.ec.modelo.notadebito;

import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.rolandopalermo.facturacion.ec.modelo.ComprobanteElectronico;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlType(name = "", propOrder = { "id", "version", "infoTributaria", "infoNotaDebito", "motivo", "campoAdicional" })
@XmlRootElement(name = "notaDebito")
public class NotaDebito extends ComprobanteElectronico {

	protected InfoNotaDebito infoNotaDebito;
	protected List<Motivo> motivo;

	@XmlElementWrapper(name = "motivos")
	public List<Motivo> getMotivo() {
		return motivo;
	}

}