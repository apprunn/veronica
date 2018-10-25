package com.rolandopalermo.facturacion.ec.web.controller;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rolandopalermo.facturacion.ec.bo.FirmadorBO;
import com.rolandopalermo.facturacion.ec.common.exception.BadRequestException;
import com.rolandopalermo.facturacion.ec.common.exception.InternalServerException;
import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.common.exception.ResourceNotFoundException;
import com.rolandopalermo.facturacion.ec.dto.FirmadorRequestDTO;
import com.rolandopalermo.facturacion.ec.dto.GenericResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/api/v1/firmar")
@Api(description = "Permite firmar un comprobante electrónico.")
public class FirmaController {

	private static final Logger logger = Logger.getLogger(FirmaController.class);

	@Autowired
	private FirmadorBO firmadorBO;

	@Value("${pkcs12.certificado.ruta}")
	private String rutaArchivoPkcs12;

	@Value("${pkcs12.certificado.clave}")
	private String claveArchivopkcs12;

	@ApiOperation(value = "Firma un comprobante electrónico")
	@PostMapping(value = "/comprobante-electronico", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<byte[]>> firmarComprobanteElectronico(
			@ApiParam(value = "Comprobante electrónico codificado como base64", required = true) 
			@RequestBody FirmadorRequestDTO request) {
		if (!new File(rutaArchivoPkcs12).exists()) {
			throw new ResourceNotFoundException("No se pudo encontrar el certificado de firma digital.");
		}
		try {
			byte[] content = firmadorBO.firmarComprobanteElectronico(request.getContenido(), rutaArchivoPkcs12,
					claveArchivopkcs12);
			return new ResponseEntity<GenericResponse<byte[]>>(new GenericResponse<byte[]>(content), HttpStatus.OK);
		} catch (NegocioException e) {
			logger.error("firmarComprobanteElectronico", e);
			throw new BadRequestException(e.getMessage());
		} catch (Exception e) {
			logger.error("firmarComprobanteElectronico", e);
			throw new InternalServerException(e.getMessage());
		}
	}

}