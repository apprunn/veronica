package com.rolandopalermo.facturacion.ec.web.controller;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rolandopalermo.facturacion.ec.bo.GeneradorBO;
import com.rolandopalermo.facturacion.ec.common.exception.BadRequestException;
import com.rolandopalermo.facturacion.ec.common.exception.InternalServerException;
import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.dto.GenericResponse;
import com.rolandopalermo.facturacion.ec.modelo.ComprobanteElectronico;
import com.rolandopalermo.facturacion.ec.modelo.factura.Factura;
import com.rolandopalermo.facturacion.ec.modelo.guia.GuiaRemision;
import com.rolandopalermo.facturacion.ec.modelo.notacredito.NotaCredito;
import com.rolandopalermo.facturacion.ec.modelo.notadebito.NotaDebito;
import com.rolandopalermo.facturacion.ec.modelo.retencion.ComprobanteRetencion;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import static com.rolandopalermo.facturacion.ec.common.util.Constantes.*;

@RestController
@RequestMapping(value = "/api/v1/generar")
@Api(description = "Genera comprobante electrónico como XML.")
public class GeneracionController {

	private static final Logger logger = Logger.getLogger(GeneracionController.class);

	@Autowired
	private GeneradorBO generadorBO;

	@ApiOperation(value = "Genera una factura en formato XML")
	@PostMapping(value = "/factura", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<byte[]>> generarFactura(
			@Valid
			@ApiParam(value = API_DOC_ANEXO_1, required = true) 
			@RequestBody Factura request) {
		return generarDocumentoElectronico(request);
	}

	@ApiOperation(value = "Genera una guía de remisión en formato XML")
	@PostMapping(value = "/guia-remision", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<byte[]>> generarGuiaRemision(
			@Valid
			@ApiParam(value = API_DOC_ANEXO_1, required = true) 
			@RequestBody GuiaRemision request) {
		return generarDocumentoElectronico(request);
	}

	@ApiOperation(value = "Genera una nota de crédito en formato XML")
	@PostMapping(value = "/nota-credito", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<byte[]>> generarNotaCredito(
			@Valid
			@ApiParam(value = API_DOC_ANEXO_1, required = true) 
			@RequestBody NotaCredito request) {
		return generarDocumentoElectronico(request);
	}

	@ApiOperation(value = "Genera una nota de débito en formato XML")
	@PostMapping(value = "/nota-debito", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<byte[]>> generarNotaDebito(
			@Valid
			@ApiParam(value = API_DOC_ANEXO_1, required = true) 
			@RequestBody NotaDebito request) {
		return generarDocumentoElectronico(request);
	}

	@ApiOperation(value = "Genera un comprobante de retención en formato XML")
	@PostMapping(value = "/comprobante-retencion", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<byte[]>> generarComprobanteRetencion(
			@Valid
			@ApiParam(value = API_DOC_ANEXO_1, required = true) 
			@RequestBody ComprobanteRetencion request) {
		return generarDocumentoElectronico(request);
	}

	private ResponseEntity<GenericResponse<byte[]>> generarDocumentoElectronico(ComprobanteElectronico request) {
		try {
			byte[] content = generadorBO.generarXMLDocumentoElectronico(request);
			return new ResponseEntity<GenericResponse<byte[]>>(new GenericResponse<byte[]>(content), HttpStatus.OK);
		} catch (NegocioException e) {
			logger.error("generarDocumentoElectronico", e);
			throw new BadRequestException(e.getMessage());
		} catch (Exception e) {
			logger.error("generarDocumentoElectronico", e);
			throw new InternalServerException(e.getMessage());
		}

	}

}