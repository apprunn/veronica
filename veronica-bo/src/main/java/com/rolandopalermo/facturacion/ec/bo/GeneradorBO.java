package com.rolandopalermo.facturacion.ec.bo;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.common.util.ArchivoUtil;
import com.rolandopalermo.facturacion.ec.modelo.ComprobanteElectronico;

@Service
public class GeneradorBO {

	private static final Logger logger = Logger.getLogger(GeneradorBO.class);
	
	public byte[] generarXMLDocumentoElectronico(ComprobanteElectronico documento) throws NegocioException {
		try {
			return ArchivoUtil.convertirJSONAXML(documento);
		} catch (Exception e) {
			logger.error("generarXMLDocumentoElectronico", e);
			throw new NegocioException("Ocurrió un error al generar el comprobante electrónico.");
		}
	}

}
