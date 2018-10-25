package com.rolandopalermo.facturacion.ec.soap.client;

import java.net.URL;

import javax.xml.namespace.QName;

import recepcion.ws.sri.gob.ec.RecepcionComprobantesOffline;
import recepcion.ws.sri.gob.ec.RecepcionComprobantesOfflineService;
import recepcion.ws.sri.gob.ec.RespuestaSolicitud;

public class EnvioComprobantesProxy {

	private RecepcionComprobantesOffline port;
	private static RecepcionComprobantesOfflineService service;

	public EnvioComprobantesProxy(String wsdlLocation) throws Exception {
		URL url = new URL(wsdlLocation);
		QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");
		service = new RecepcionComprobantesOfflineService(url, qname);
		port = service.getRecepcionComprobantesOfflinePort();
	}

	public RespuestaSolicitud enviarComprobante(byte[] archivoBytes) throws Exception {
		RespuestaSolicitud response = null;
		if (archivoBytes != null) {
			response = port.validarComprobante(archivoBytes);
		}
		return response;
	}

}