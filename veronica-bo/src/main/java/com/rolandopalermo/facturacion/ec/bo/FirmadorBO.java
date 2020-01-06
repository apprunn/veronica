package com.rolandopalermo.facturacion.ec.bo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.common.sri.Firmador;

@Service
public class FirmadorBO {

	private static final Logger logger = Logger.getLogger(FirmadorBO.class);

	/**
	 * 
	 * @param comprobanteElectronico
	 * @param rutaArchivoPkcs12
	 * @param claveArchivopkcs12
	 * @return
	 * @throws NegocioException
	 */
	public byte[] firmarComprobanteElectronico(byte[] comprobanteElectronico, String rutaArchivoPkcs12,
			String claveArchivopkcs12) throws NegocioException {
		if (comprobanteElectronico == null || comprobanteElectronico.length == 0) {
			throw new NegocioException("El documento XML no tiene una estructura válida.");
		}
		try {
			// Actividad 1.- Generar archivo temporales para el XML y su respectivo archivo
			// firmado
			String rutaArchivoXML = UUID.randomUUID().toString();
			File temp = File.createTempFile(rutaArchivoXML, ".xml");
			rutaArchivoXML = temp.getAbsolutePath();
			String rutaArchivoXMLFirmado = UUID.randomUUID().toString();
			File tempFirmado = File.createTempFile(rutaArchivoXMLFirmado, ".xml");
			rutaArchivoXMLFirmado = tempFirmado.getAbsolutePath();
			// Actividad 2.- Guardar datos en archivo xml
			try (FileOutputStream fos = new FileOutputStream(rutaArchivoXML)) {
				fos.write(comprobanteElectronico);
			}
			// Actividad 3.- Firmar el archivo xml creado temporalmente
			Firmador firmador = new Firmador(rutaArchivoXML, rutaArchivoXMLFirmado, rutaArchivoPkcs12,
					claveArchivopkcs12);
			firmador.firmar();
			// 4.- Obtener el contenido del archivo XML
			Path path = Paths.get(rutaArchivoXMLFirmado);
			byte[] data = Files.readAllBytes(path);
			if (!temp.delete() || !tempFirmado.delete()) {
				throw new Exception("No se pudo eliminar los archivos temporales.");
			}
			return data;
		} catch (IOException e) {
			logger.error("firmarComprobanteElectronico", e);
			throw new NegocioException(e.getMessage());
		} catch (Exception e) {
			logger.error("firmarComprobanteElectronico", e);
			throw new NegocioException("Ocurrió un error al generar el comprobante electrónico.");
		}
	}
	
	public boolean verifySignature(byte [] signature, String password) {
		return Firmador.verifyPKSStore(signature, password);
	}
}