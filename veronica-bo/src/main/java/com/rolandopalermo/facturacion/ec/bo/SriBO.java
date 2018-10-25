package com.rolandopalermo.facturacion.ec.bo;

import java.io.File;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.common.util.ArchivoUtil;
import com.rolandopalermo.facturacion.ec.common.util.MarshallerUtil;
import com.rolandopalermo.facturacion.ec.modelo.ComprobanteElectronico;
import com.rolandopalermo.facturacion.ec.soap.client.AutorizacionComprobanteProxy;
import com.rolandopalermo.facturacion.ec.soap.client.EnvioComprobantesProxy;

import autorizacion.ws.sri.gob.ec.RespuestaComprobante;
import recepcion.ws.sri.gob.ec.RespuestaSolicitud;

@Service
public class SriBO {

	private static final Logger logger = Logger.getLogger(SriBO.class);

	@Autowired
	private FirmadorBO firmadorBO;

	public RespuestaSolicitud enviarComprobante(byte[] xml, String wsdlRecepcion) throws NegocioException {
		try {
			EnvioComprobantesProxy proxy = new EnvioComprobantesProxy(wsdlRecepcion);
			return proxy.enviarComprobante(xml);
		} catch (Exception e) {
			logger.error("enviarComprobante", e);
			throw new NegocioException("No se pudo enviar el comprobante electrónico al SRI.");
		}
	}

	public RespuestaComprobante autorizarComprobante(String claveAcceso, String wsdlAutorizacion)
			throws NegocioException {
		try {
			AutorizacionComprobanteProxy proxy = new AutorizacionComprobanteProxy(wsdlAutorizacion);
			return proxy.autorizacionIndividual(claveAcceso);
		} catch (Exception e) {
			logger.error("autorizarComprobante", e);
			throw new NegocioException(
					String.format("No se pudo autorizar el comprobante electrónico [%s].", claveAcceso));
		}
	}

	public RespuestaComprobante emitirComprobante(ComprobanteElectronico comprobante, String rutaArchivoPkcs12,
			String claveArchivopkcs12, String wsdlRecepcion, String wsdlAutorizacion) throws NegocioException {
		try {
			// Actividad 1.- Crear archivo temporal para el xml
			String rutaArchivoXML = UUID.randomUUID().toString();
			File temp = File.createTempFile(rutaArchivoXML, ".xml");
			rutaArchivoXML = temp.getAbsolutePath();
			// Actividad 2.- Ejecutar Marshalling
			MarshallerUtil.marshall(comprobante, rutaArchivoXML);
			// Actividad 3.- Firmar el archivo
			byte[] xml = firmadorBO.firmarComprobanteElectronico(ArchivoUtil.convertirArchivoAByteArray(temp),
					rutaArchivoPkcs12, claveArchivopkcs12);
			// Actividad 4.- Enviar a Recepción
			RespuestaSolicitud respuestaSolicitud = enviarComprobante(xml, wsdlRecepcion);
			if (respuestaSolicitud != null && respuestaSolicitud.getEstado() != null
					&& respuestaSolicitud.getEstado().toUpperCase().compareTo("RECIBIDA") == 0) {
				// 5.- Autorizar comprobante
				String claveAcceso = comprobante.getInfoTributaria().getClaveAcceso();
				RespuestaComprobante respuestaComprobante = autorizarComprobante(claveAcceso, wsdlAutorizacion);
				if(!temp.delete()) {
					throw new NegocioException("No se pudo eliminar el archivo temporal.");
				}
				return respuestaComprobante;
			} else {
				// loguear error
				throw new NegocioException("No se pudo autorizar el comprobante electrónico.");
			}
		} catch (Exception e) {
			throw new NegocioException("No se pudo autorizar el comprobante electrónico.");
		}
	}

}
