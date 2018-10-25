package com.rolandopalermo.facturacion.ec.soap.client;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import autorizacion.ws.sri.gob.ec.AutorizacionComprobantesOffline;
import autorizacion.ws.sri.gob.ec.AutorizacionComprobantesOfflineService;
import autorizacion.ws.sri.gob.ec.RespuestaComprobante;

public class AutorizacionComprobanteProxy {

	private AutorizacionComprobantesOfflineService service;
	private AutorizacionComprobantesOffline port;

	public AutorizacionComprobanteProxy(String wsdlLocation) throws Exception {
		service = new AutorizacionComprobantesOfflineService(new URL(wsdlLocation),
				new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService"));
		port = service.getAutorizacionComprobantesOfflinePort();
		((BindingProvider) port).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", 10000);
		((BindingProvider) port).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", 10000);
	}

	public RespuestaComprobante autorizacionIndividual(String claveDeAcceso) throws Exception {
		RespuestaComprobante response = null;
		response = port.autorizacionComprobante(claveDeAcceso);
		return response;
	}

}